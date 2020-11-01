package dev.mlnr.spidey.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.Core;
import dev.mlnr.spidey.cache.MusicPlayerCache;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MusicPlayer
{
    private final TrackScheduler trackScheduler;
    private final AudioPlayer audioPlayer;

    private ScheduledFuture<?> leaveTask;

    public MusicPlayer(final long guildId)
    {
        this.audioPlayer = MusicUtils.getAudioPlayerManager().createPlayer();
        this.trackScheduler = new TrackScheduler(this.audioPlayer, guildId);
    }

    public TrackScheduler getTrackScheduler()
    {
        return this.trackScheduler;
    }

    // leave task

    public void scheduleLeave()
    {
        if (leaveTask != null)
            return;
        leaveTask = Core.getExecutor().schedule(() -> MusicPlayerCache.destroyMusicPlayer(trackScheduler.getGuild()), 2, TimeUnit.MINUTES);
    }

    public void cancelLeave()
    {
        if (leaveTask == null)
            return;
        leaveTask.cancel(true);
        leaveTask = null;
    }

    // AudioPlayer wrapper methods

    public AudioTrack getPlayingTrack()
    {
        return audioPlayer.getPlayingTrack();
    }

    public void pause()
    {
        audioPlayer.setPaused(true);
    }

    public void unpause()
    {
        audioPlayer.setPaused(false);
    }

    public void destroyAudioPlayer()
    {
        audioPlayer.destroy();
    }

    // track scheduler wrapper methods

    public void skip()
    {
        trackScheduler.nextTrack();
    }

    // other

    public AudioSendHandler getAudioSendHandler()
    {
        return new AudioPlayerSendHandler(this.audioPlayer);
    }
}