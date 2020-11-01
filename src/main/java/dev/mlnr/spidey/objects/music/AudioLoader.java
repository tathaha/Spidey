package dev.mlnr.spidey.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.MusicUtils;

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
        loadSingle(track, false);
    }

    @Override
    public void playlistLoaded(final AudioPlaylist playlist)
    {
        ctx.reactLike();
        final var tracks = playlist.getTracks();
        if (playlist.isSearchResult())
        {
            loadSingle(tracks.get(0), false);
            return;
        }
        tracks.forEach(track -> loadSingle(track, true));
        ctx.reply("**" + tracks.size() + "** tracks from playlist **" + playlist.getName() + "** have been added to the queue. [" + ctx.getAuthor().getAsMention() + "]", null);
    }

    @Override
    public void noMatches()
    {
        ctx.replyError("No matches found for **" + this.query + "**");
    }

    @Override
    public void loadFailed(final FriendlyException exception)
    {
        System.out.println(exception.getMessage());
    }

    private void loadSingle(final AudioTrack track, final boolean silent)
    {
        final var requester = ctx.getAuthor();
        track.setUserData(requester.getIdLong());
        final var trackInfo = track.getInfo();
        final var trackScheduler = musicPlayer.getTrackScheduler();
        final var title = trackInfo.title;
        final var author = trackInfo.author;
        final var length = trackInfo.length;
        final var queue = trackScheduler.getQueue();

        if (queue.stream().filter(queued -> trackInfo.uri.equals(queued.getInfo().uri)).count() > MusicUtils.getMaxFairQueue() && !silent)
        {
            ctx.replyError("Fair queue limit has been reached! Don't queue the same song over and over again");
            return;
        }
        trackScheduler.queue(track, this.insertFirst);

        if (silent)
            return;
        ctx.reactLike();
        ctx.reply("Track **" + title + "** (**" + MusicUtils.formatDuration(length) + "**) by **" + author + "** has been added to the queue. [" + requester.getAsMention() + "]", null);
    }
}