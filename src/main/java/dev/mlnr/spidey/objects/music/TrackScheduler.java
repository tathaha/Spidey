package dev.mlnr.spidey.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import dev.mlnr.spidey.Core;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import static dev.mlnr.spidey.utils.MusicUtils.handleMarkers;

public class TrackScheduler extends AudioEventAdapter
{
    private final AudioPlayer audioPlayer;
    private final long guildId;

    private final Deque<AudioTrack> queue;
    private RepeatMode repeatMode;

    private AudioTrack previousTrack;
    private AudioTrack currentTrack;

    public TrackScheduler(final AudioPlayer audioPlayer, final long guildId)
    {
        this.audioPlayer = audioPlayer;
        audioPlayer.addListener(this);
        this.guildId = guildId;
        this.queue = new ConcurrentLinkedDeque<>();
    }

    public void queue(final AudioTrack track)
    {
        queue(track, false);
    }

    public void queue(final AudioTrack track, final boolean addFirst)
    {
        if (audioPlayer.getPlayingTrack() == null)
        {
            audioPlayer.playTrack(track);
            currentTrack = track;
            return;
        }
        if (addFirst)
            queue.addFirst(track);
        else
            queue.offer(track);
    }

    public void nextTrack()
    {
        if (repeatMode == RepeatMode.SONG && currentTrack != null)
        {
            final var currentCloned = currentTrack.makeClone();
            handleMarkers(currentCloned, this);
            queue(currentCloned);
            return;
        }

        if (currentTrack != null)
            previousTrack = currentTrack;

        final var nextTrack = queue.poll();
        currentTrack = nextTrack;

        final var trackToPlay = nextTrack != null ? nextTrack : (repeatMode == RepeatMode.QUEUE ? previousTrack.makeClone() : null);
        if (trackToPlay != null)
            handleMarkers(trackToPlay, this);
        audioPlayer.playTrack(trackToPlay);
    }

    public Deque<AudioTrack> getQueue()
    {
        return this.queue;
    }

    public RepeatMode getRepeatMode()
    {
        return this.repeatMode;
    }

    public void setRepeatMode(final RepeatMode repeatMode)
    {
        this.repeatMode = repeatMode;
    }

    public Guild getGuild()
    {
        return Core.getJDA().getGuildById(this.guildId);
    }

    public long getGuildId()
    {
        return this.guildId;
    }

    @Override
    public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason)
    {
        if (endReason.mayStartNext)
            nextTrack();
    }

    public enum RepeatMode
    {
        SONG,
        QUEUE
    }
}