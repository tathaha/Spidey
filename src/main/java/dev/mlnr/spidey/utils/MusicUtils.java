package dev.mlnr.spidey.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.cache.music.VideoSegmentCache;
import dev.mlnr.spidey.handlers.music.SegmentHandler;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.music.MusicPlayer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static dev.mlnr.spidey.utils.MusicUtils.ConnectFailureReason.*;
import static dev.mlnr.spidey.utils.MusicUtils.LoadFailureReason.*;

public class MusicUtils
{
    public static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile("^(https?://)?((www|m)\\.)?youtu(\\.be|be\\.com)/(playlist\\?list=([a-zA-Z0-9-_]+))?((watch\\?v=)?([a-zA-Z0-9-_]{11})(&list=([a-zA-Z0-9-_]+))?)?");

    public static final int MAX_QUEUE_SIZE = 150;

    private static final int MAX_TRACK_LENGTH_HOURS = 2;
    public static final long MAX_TRACK_LENGTH_MILLIS = TimeUnit.HOURS.toMillis(MAX_TRACK_LENGTH_HOURS);

    private static final AudioPlayerManager AUDIO_PLAYER_MANAGER = new DefaultAudioPlayerManager();

    private static final int MAX_FAIR_QUEUE = 3;

    private static final String BLOCK_INACTIVE = "\u25AC";
    private static final String BLOCK_ACTIVE = "\uD83D\uDD18";

    private MusicUtils() {}

    static
    {
        AUDIO_PLAYER_MANAGER.registerSourceManager(new YoutubeAudioSourceManager());
        AudioSourceManagers.registerRemoteSources(AUDIO_PLAYER_MANAGER);
    }

    public static ConnectFailureReason checkVoiceChannel(final CommandContext ctx)
    {
        final var guild = ctx.getGuild();
        final var voiceState = ctx.getMember().getVoiceState();
        if (voiceState == null)
            return NO_CHANNEL;
        final var voiceChannel = voiceState.getChannel();
        if (voiceChannel == null)
            return NO_CHANNEL;

        if (guild.getAudioManager().isConnected())
            return null;

        final var selfMember = guild.getSelfMember();
        if (!selfMember.hasAccess(voiceChannel))
            return NO_PERMS;
        if (!selfMember.hasPermission(voiceChannel, Permission.VOICE_SPEAK))
            return CANT_SPEAK;
        final var userLimit = voiceChannel.getUserLimit();
        if (userLimit != 0 && voiceChannel.getMembers().size() >= userLimit)
            return CHANNEL_FULL;
        connectToVoiceChannel(voiceChannel);
        return null;
    }

    private static void connectToVoiceChannel(final VoiceChannel voiceChannel)
    {
        voiceChannel.getGuild().getAudioManager().openAudioConnection(voiceChannel);
    }

    public static AudioPlayerManager getAudioPlayerManager()
    {
        return AUDIO_PLAYER_MANAGER;
    }

    public static String formatDuration(final long time)
    {
        final var duration = Duration.ofMillis(time);
        final var hours = duration.toHoursPart();
        final var minutes = duration.toMinutesPart();
        final var seconds = duration.toSecondsPart();
        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static boolean canInteract(final Member member, final AudioTrack track)
    {
        return getRequesterId(track) == member.getIdLong() || canInteract(member);
    }

    public static boolean canInteract(final Member member)
    {
        return member.hasPermission(Permission.MANAGE_SERVER) || isDJ(member);
    }

    public static boolean isDJ(final Member member)
    {
        final var djRoleId = GuildSettingsCache.getDJRoleId(member.getGuild().getIdLong());
        return djRoleId != 0 && member.getRoles().stream().anyMatch(role -> role.getIdLong() == djRoleId);
    }

    public static String getProgressBar(final long position, final long duration)
    {
        final var activeBlocks = (int) ((float) position / duration * 15);
        final var progressBuilder = new StringBuilder();
        for (var i = 0; i < 15; i++)
            progressBuilder.append(i == activeBlocks ? BLOCK_ACTIVE : BLOCK_INACTIVE);
        return progressBuilder.append(BLOCK_INACTIVE).append(" [**").append(formatDuration(position)).append("/").append(formatDuration(duration)).append("**]").toString();
    }

    public static void handleMarkers(final AudioTrack track, final long guildId)
    {
        if (!GuildSettingsCache.isSegmentSkippingEnabled(guildId))
            return;
        final var segments = VideoSegmentCache.getVideoSegments(track.getIdentifier());
        if (!segments.isEmpty())
            track.setMarker(new TrackMarker(segments.get(0).getSegmentStart(), new SegmentHandler(track)));
    }

    public static long getLengthWithoutSegments(final AudioTrack track, final long guildId)
    {
        final var length = track.getInfo().length;
        if (!GuildSettingsCache.isSegmentSkippingEnabled(guildId))
            return length;
        final var segments = VideoSegmentCache.getVideoSegments(track.getIdentifier());
        return segments.isEmpty() ? length : length - segments.stream().mapToLong(segment -> segment.getSegmentEnd() - segment.getSegmentStart()).sum();
    }

    public static VoiceChannel getConnectedChannel(final Guild guild)
    {
        return guild.getAudioManager().getConnectedChannel();
    }

    public static boolean isMemberConnected(final CommandContext ctx)
    {
        return getConnectedChannel(ctx.getGuild()).getMembers().contains(ctx.getMember());
    }

    public static long getRequesterId(final AudioTrack track)
    {
        return track.getUserData(Long.class);
    }

    public static LoadFailureReason checkTrack(final AudioTrack track, final MusicPlayer musicPlayer, final long guildId)
    {
        final var queue = musicPlayer.getTrackScheduler().getQueue();
        final var trackInfo = track.getInfo();
        final var isVip = GuildSettingsCache.isVip(guildId);
        if (queue.stream().filter(queued -> trackInfo.uri.equals(queued.getInfo().uri)).count() == MAX_FAIR_QUEUE)
            return FAIR_QUEUE;
        if (!isVip && queue.size() == MAX_QUEUE_SIZE)
            return QUEUE_FULL;
        if (!isVip && trackInfo.length > MAX_TRACK_LENGTH_MILLIS)
            return TRACK_LONG;
        return null;
    }

    public enum ConnectFailureReason
    {
        NO_CHANNEL("you're not connected to any channel"),
        NO_PERMS("i don't have permission to join your channel"),
        CHANNEL_FULL("the voice channel you're in is full"),
        CANT_SPEAK("i can't speak in your channel");

        private final String reason;

        ConnectFailureReason(final String reason)
        {
            this.reason = reason;
        }

        public String getReason()
        {
            return this.reason;
        }
    }

    public enum LoadFailureReason
    {
        QUEUE_FULL("the max queue size of **" + MAX_QUEUE_SIZE + "** tracks has been reached! To completely remove this limit, you can purchase permanent VIP for a symbolic price of 2€"),
        TRACK_LONG("the track is at least **" + MAX_TRACK_LENGTH_HOURS + "** hours long. To completely remove the length limit, you can purchase permanent VIP for a symbolic price of 2€"),
        FAIR_QUEUE("the fair queue limit of **" + MAX_FAIR_QUEUE + "** tracks has been reached! Don't queue the same song over and over again");

        private final String reason;

        LoadFailureReason(final String reason)
        {
            this.reason = reason;
        }

        public String getReason()
        {
            return this.reason;
        }
    }
}