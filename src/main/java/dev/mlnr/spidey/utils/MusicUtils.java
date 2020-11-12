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
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.time.Duration;
import java.util.regex.Pattern;

import static dev.mlnr.spidey.utils.MusicUtils.ConnectFailureReason.*;

public class MusicUtils
{
    public static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile("^(https?://)?((www|m)\\.)?youtu(\\.be|be\\.com)/(playlist\\?list=([a-zA-Z0-9-_]+))?((watch\\?v=)?([a-zA-Z0-9-_]{11})(&list=([a-zA-Z0-9-_]+))?)?");

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

    public static int getMaxFairQueue()
    {
        return MAX_FAIR_QUEUE;
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
        if (segments != null)
            track.setMarker(new TrackMarker((long) segments.keySet().toArray()[0], new SegmentHandler(track)));
    }

    public static long getLengthWithoutSegments(final AudioTrack track, final long guildId)
    {
        final var length = track.getInfo().length;
        if (!GuildSettingsCache.isSegmentSkippingEnabled(guildId))
            return length;
        final var segments = VideoSegmentCache.getVideoSegments(track.getIdentifier());
        if (segments == null)
            return length;
        return length - segments.entrySet().stream().mapToLong(segment -> segment.getValue() - segment.getKey()).sum();
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
}