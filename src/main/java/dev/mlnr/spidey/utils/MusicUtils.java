package dev.mlnr.spidey.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.cache.music.DJRoleCache;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;
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

    public static String formatDuration(final long length)
    {
        final var duration = Duration.ofMillis(length);
        return String.format("%02d:%02d", duration.toMinutesPart(), duration.toSecondsPart());
    }

    public static boolean canInteract(final Member member, final AudioTrack track)
    {
        return canInteract(member) || track.getUserData(Long.class) == member.getIdLong();
    }

    public static boolean canInteract(final Member member)
    {
        return member.hasPermission(Permission.MANAGE_SERVER) || isDJ(member);
    }

    public static boolean isDJ(final Member member)
    {
        return member.getRoles().stream().anyMatch(role -> role.getIdLong() == DJRoleCache.getDJRole(member.getGuild().getIdLong()));
    }

    public static String getProgressBar(final long position, final long duration)
    {
        final var activeBlocks = (int) ((float) position / duration * 10);
        final var progressBuilder = new StringBuilder();
        for (var i = 0; i < 10; i++)
            progressBuilder.append(activeBlocks == i ? BLOCK_ACTIVE : BLOCK_INACTIVE);
        return progressBuilder.append(BLOCK_INACTIVE).append(" [**").append(formatDuration(position)).append("/").append(formatDuration(duration)).append("**]").toString();
    }

    public enum ConnectFailureReason
    {
        NO_CHANNEL("you're not connected to any channel"),
        NO_PERMS("i don't have permissions to join your channel"),
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