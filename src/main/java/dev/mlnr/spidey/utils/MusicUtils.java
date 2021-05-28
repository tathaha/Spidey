package dev.mlnr.spidey.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.cache.music.VideoSegmentCache;
import dev.mlnr.spidey.handlers.music.SegmentHandler;
import dev.mlnr.spidey.objects.I18n;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.music.MusicPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static dev.mlnr.spidey.utils.MusicUtils.ConnectFailureReason.*;
import static dev.mlnr.spidey.utils.MusicUtils.LoadFailureReason.*;

public class MusicUtils {
	public static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile("^(https?://)?((www|music|m)\\.)?youtu(\\.be|be\\.com)/(playlist\\?list=([a-zA-Z0-9-_]+))?((watch\\?v=)?([a-zA-Z0-9-_]{11})(&list=([a-zA-Z0-9-_]+))?)?");

	public static final int MAX_FAIR_QUEUE = 3;
	public static final int MAX_QUEUE_SIZE = 150;
	private static final int MAX_TRACK_LENGTH_HOURS = 2;
	public static final long MAX_TRACK_LENGTH_MILLIS = TimeUnit.HOURS.toMillis(MAX_TRACK_LENGTH_HOURS);
	private static final AudioPlayerManager AUDIO_PLAYER_MANAGER = new DefaultAudioPlayerManager();

	private static final int BLOCK_AMOUNT = 15;
	private static final String BLOCK_INACTIVE = "\u25AC";
	private static final String BLOCK_ACTIVE = "\uD83D\uDD18";

	private MusicUtils() {}

	public static void registerSources() {
		AUDIO_PLAYER_MANAGER.registerSourceManager(new YoutubeAudioSourceManager());
		AudioSourceManagers.registerRemoteSources(AUDIO_PLAYER_MANAGER);
	}

	private static ConnectFailureReason checkVoiceChannel(CommandContext ctx) {
		var guild = ctx.getGuild();
		var voiceState = ctx.getMember().getVoiceState();
		if (voiceState == null) {
			return NO_CHANNEL;
		}
		var voiceChannel = voiceState.getChannel();
		if (voiceChannel == null) {
			return NO_CHANNEL;
		}

		if (guild.getAudioManager().isConnected()) {
			return null;
		}

		var selfMember = guild.getSelfMember();
		if (!selfMember.hasAccess(voiceChannel)) {
			return NO_PERMS;
		}
		if (!selfMember.hasPermission(voiceChannel, Permission.VOICE_SPEAK)) {
			return CANT_SPEAK;
		}
		var userLimit = voiceChannel.getUserLimit();
		if (userLimit != 0 && voiceChannel.getMembers().size() >= userLimit) {
			return CHANNEL_FULL;
		}
		connectToVoiceChannel(voiceChannel);
		return null;
	}

	private static void connectToVoiceChannel(VoiceChannel voiceChannel) {
		voiceChannel.getGuild().getAudioManager().openAudioConnection(voiceChannel);
	}

	public static AudioPlayerManager getAudioPlayerManager() {
		return AUDIO_PLAYER_MANAGER;
	}

	public static String formatDuration(long time) {
		var duration = Duration.ofMillis(time);
		var hours = duration.toHoursPart();
		var minutes = duration.toMinutesPart();
		var seconds = duration.toSecondsPart();
		if (hours > 0) {
			return String.format("%02d:%02d:%02d", hours, minutes, seconds);
		}
		return String.format("%02d:%02d", minutes, seconds);
	}

	public static boolean canInteract(Member member, AudioTrack track) {
		return getRequesterId(track) == member.getIdLong() || canInteract(member);
	}

	public static boolean canInteract(Member member) {
		return member.hasPermission(Permission.MANAGE_SERVER) || isDJ(member);
	}

	public static boolean isDJ(Member member) {
		var djRoleId = GuildSettingsCache.getInstance().getMusicSettings(member.getGuild().getIdLong()).getDJRoleId();
		return djRoleId != 0 && member.getRoles().stream().anyMatch(role -> role.getIdLong() == djRoleId);
	}

	public static String getProgressBar(long position, long duration) {
		var activeBlocks = (int) ((float) position / duration * BLOCK_AMOUNT);
		var progressBuilder = new StringBuilder();
		for (var i = 0; i < BLOCK_AMOUNT; i++)
			progressBuilder.append(i == activeBlocks ? BLOCK_ACTIVE : BLOCK_INACTIVE);
		return progressBuilder.append(BLOCK_INACTIVE).append(" [**").append(formatDuration(position)).append("/").append(formatDuration(duration)).append("**]").toString();
	}

	public static void handleMarkers(AudioTrack track, long guildId) {
		if (!GuildSettingsCache.getInstance().getMusicSettings(guildId).isSegmentSkippingEnabled()) {
			return;
		}
		var segments = VideoSegmentCache.getInstance().getVideoSegments(track.getIdentifier());
		if (!segments.isEmpty()) {
			track.setMarker(new TrackMarker(segments.get(0).getSegmentStart(), new SegmentHandler(track)));
		}
	}

	public static long getLengthWithoutSegments(AudioTrack track, long guildId) {
		var length = track.getInfo().length;
		if (!GuildSettingsCache.getInstance().getMusicSettings(guildId).isSegmentSkippingEnabled()) {
			return length;
		}
		var segments = VideoSegmentCache.getInstance().getVideoSegments(track.getIdentifier());
		return segments.isEmpty() ? length : length - segments.stream().mapToLong(segment -> segment.getSegmentEnd() - segment.getSegmentStart()).sum();
	}

	public static VoiceChannel getConnectedChannel(Guild guild) {
		return guild.getAudioManager().getConnectedChannel();
	}

	public static boolean isMemberConnected(CommandContext ctx) {
		return getConnectedChannel(ctx.getGuild()).getMembers().contains(ctx.getMember());
	}

	public static long getRequesterId(AudioTrack track) {
		return track.getUserData(Long.class);
	}

	public static LoadFailureReason checkTrack(AudioTrack track, MusicPlayer musicPlayer, long guildId) {
		var queue = musicPlayer.getTrackScheduler().getQueue();
		var trackInfo = track.getInfo();
		var fairQueueThreshold = getFairQueueThreshold(guildId);
		if (fairQueueThreshold != -1 && queue.stream().filter(queued -> trackInfo.uri.equals(queued.getInfo().uri)).count() == fairQueueThreshold) {
			return FAIR_QUEUE;
		}
		if (GuildSettingsCache.getInstance().getGeneralSettings(guildId).isVip()) {
			return null;
		}
		if (queue.size() == MAX_QUEUE_SIZE) {
			return QUEUE_FULL;
		}
		if (trackInfo.length > MAX_TRACK_LENGTH_MILLIS) {
			return TRACK_LONG;
		}
		return null;
	}

	public static String formatLength(long originalLength, long lengthWithoutSegments, I18n i18n) {
		var durationBuilder = new StringBuilder().append("(**").append(formatDuration(originalLength)).append("**");
		if (lengthWithoutSegments != originalLength) {
			durationBuilder.append(" [**").append(formatDuration(lengthWithoutSegments))
					.append("** ").append(i18n.get("music.messages.without_segments")).append("]");
		}
		durationBuilder.append(")");
		return durationBuilder.toString();
	}

	public static EmbedBuilder createMusicResponseBuilder() {
		return new EmbedBuilder().setColor(Utils.SPIDEY_COLOR);
	}

	private static int getFairQueueThreshold(long guildId) {
		var musicSettings = GuildSettingsCache.getInstance().getMusicSettings(guildId);
		return musicSettings.isFairQueueEnabled() ? musicSettings.getFairQueueThreshold() : -1;
	}

	public static void loadQuery(MusicPlayer musicPlayer, String query, AudioLoadResultHandler loader) {
		AUDIO_PLAYER_MANAGER.loadItemOrdered(musicPlayer, query, loader);
	}

	public static MusicPlayer checkPlayability(CommandContext ctx) {
		var i18n = ctx.getI18n();
		var connectionFailure = checkVoiceChannel(ctx);
		if (connectionFailure != null) {
			ctx.replyError(i18n.get("music.messages.failure.connect.cant_play")
					+ " " + i18n.get("music.messages.failure.connect." + connectionFailure.name().toLowerCase() + "."));
			return null;
		}
		var musicPlayer = MusicPlayerCache.getInstance().getMusicPlayer(ctx.getGuild(), true);
		var trackScheduler = musicPlayer.getTrackScheduler();

		if (trackScheduler.getQueue().isEmpty()) {
			trackScheduler.setRepeatMode(null);
		}
		return musicPlayer;
	}

	public static String formatTrack(AudioTrack track) {
		var trackInfo = track.getInfo();
		return "[`" + trackInfo.title + "`](" + trackInfo.uri + ") (**" + formatDuration(trackInfo.length) + "**)";
	}

	public static String formatLoadError(LoadFailureReason loadFailureReason, CommandContext ctx) {
		var i18n = ctx.getI18n();
		var message = i18n.get("music.messages.failure.load.cant_load") + " ";
		switch (loadFailureReason) {
			case QUEUE_FULL:
				message += i18n.get("music.messages.failure.load.queue_full", MAX_QUEUE_SIZE);
				break;
			case TRACK_LONG:
				message += i18n.get("music.messages.failure.load.track_long", MAX_TRACK_LENGTH_HOURS);
				break;
			case FAIR_QUEUE:
				message += i18n.get("music.messages.failure.load.fair_queue", MAX_FAIR_QUEUE);
				break;
		}
		return message;
	}

	public enum ConnectFailureReason {
		NO_CHANNEL,
		NO_PERMS,
		CHANNEL_FULL,
		CANT_SPEAK
	}

	public enum LoadFailureReason {
		QUEUE_FULL,
		TRACK_LONG,
		FAIR_QUEUE
	}
}