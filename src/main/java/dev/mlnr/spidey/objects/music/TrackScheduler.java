package dev.mlnr.spidey.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import static dev.mlnr.spidey.utils.MusicUtils.handleMarkers;

public class TrackScheduler extends AudioEventAdapter
{
    private final AudioPlayer audioPlayer;
    private final long guildId;

    private final ConcurrentLinkedDeque<AudioTrack> queue;
    private RepeatMode repeatMode;

    private AudioTrack previousTrack;
    private AudioTrack currentTrack;

    private final List<Long> skipVotes = new ArrayList<>();

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
        clearSkipVotes();
        if (repeatMode == RepeatMode.SONG && currentTrack != null)
        {
            final var currentCloned = currentTrack.makeClone();
            handleMarkers(currentCloned, this.guildId);
            queue(currentCloned);
            return;
        }

        if (currentTrack != null)
            previousTrack = currentTrack;

        final var nextTrack = queue.poll();
        currentTrack = nextTrack;

        final var trackToPlay = nextTrack != null ? nextTrack : (repeatMode == RepeatMode.QUEUE ? previousTrack.makeClone() : null);
        if (trackToPlay != null)
            handleMarkers(trackToPlay, this.guildId);
        audioPlayer.playTrack(trackToPlay);
    }

    public ConcurrentLinkedDeque<AudioTrack> getQueue()
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
        return Spidey.getJDA().getGuildById(this.guildId);
    }

    public long getGuildId()
    {
        return this.guildId;
    }

    // skip voting

    public int getRequiredSkipVotes()
    {
        final var listeners = MusicUtils.getConnectedChannel(getGuild()).getMembers().stream()
                .filter(member -> !member.getUser().isBot() && !member.getVoiceState().isDeafened())
                .count();
        return (int) Math.ceil(listeners * 0.55);
    }

    public int getSkipVotes()
    {
        return skipVotes.size();
    }

    public void addSkipVote(final User user)
    {
        skipVotes.add(user.getIdLong());
    }

    public void removeSkipVote(final User user)
    {
        skipVotes.remove(user.getIdLong());
    }

    public boolean hasSkipVoted(final User user)
    {
        return skipVotes.contains(user.getIdLong());
    }

    public void clearSkipVotes()
    {
        skipVotes.clear();
    }

    // events

    @Override
    public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason)
    {
        if (endReason.mayStartNext)
            nextTrack();
    }

    // other

    public enum RepeatMode
    {
        SONG,
        QUEUE
    }
}