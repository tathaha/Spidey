package dev.mlnr.spidey.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.MusicUtils;

import static dev.mlnr.spidey.utils.MusicUtils.formatDuration;

public class AudioLoader implements AudioLoadResultHandler
{
    private final MusicPlayer musicPlayer;
    private final String query;
    private final CommandContext ctx;
    private final boolean insertFirst;

    public AudioLoader(final MusicPlayer musicPlayer, final String query, final CommandContext ctx, final boolean insertFirst)
    {
        this.musicPlayer = musicPlayer;
        this.query = query;
        this.ctx = ctx;
        this.insertFirst = insertFirst;
    }

    @Override
    public void trackLoaded(final AudioTrack track)
    {
        loadSingle(track);
    }

    @Override
    public void playlistLoaded(final AudioPlaylist playlist)
    {
        final var tracks = playlist.getTracks();
        if (playlist.isSearchResult())
        {
            loadSingle(tracks.get(0));
            return;
        }
        tracks.forEach(track -> loadSingle(track, true));
        final var length = tracks.stream().mapToLong(track -> track.getInfo().length).sum();
        ctx.reactLike();
        ctx.reply("**" + tracks.size() + "** tracks from playlist **" + playlist.getName() + "** (**" + formatDuration(length) + "**) have been added to the queue. [" + ctx.getAuthor().getAsMention()
                + "]", null);
    }

    @Override
    public void noMatches()
    {
        ctx.replyError("No matches found for **" + (query.startsWith("ytsearch:") ? query.substring(9) : query) + "**", Emojis.DISLIKE);
    }

    @Override
    public void loadFailed(final FriendlyException exception)
    {
        ctx.replyError("There was an error while loading the track", Emojis.DISLIKE);
    }

    private void loadSingle(final AudioTrack track)
    {
        loadSingle(track, false);
    }

    private void loadSingle(final AudioTrack track, final boolean silent)
    {
        final var requester = ctx.getAuthor();
        final var trackInfo = track.getInfo();
        final var trackScheduler = musicPlayer.getTrackScheduler();
        final var title = trackInfo.title;
        final var channel = trackInfo.author;
        final var length = trackInfo.length;
        final var stream = trackInfo.isStream;
        final var queue = trackScheduler.getQueue();

        if (!silent && queue.stream().filter(queued -> trackInfo.uri.equals(queued.getInfo().uri)).count() > MusicUtils.getMaxFairQueue())
        {
            ctx.replyError("Fair queue limit has been reached! Don't queue the same song over and over again");
            return;
        }

        MusicUtils.handleMarkers(track, trackScheduler);
        track.setUserData(requester.getIdLong());
        trackScheduler.queue(track, this.insertFirst);

        if (silent)
            return;
        ctx.reactLike();
        ctx.reply((stream ? "Livestream" : "Track") + " **" + title + "**" + (stream ? "" : " (**" + formatDuration(length) + "**)") + " from channel **" + channel + "**"
                + " has been added to the queue. [" + requester.getAsMention() + "]", null);
    }
}