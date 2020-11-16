package dev.mlnr.spidey.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.MusicUtils;

import static dev.mlnr.spidey.utils.MusicUtils.LoadFailureReason.FAIR_QUEUE;
import static dev.mlnr.spidey.utils.MusicUtils.LoadFailureReason.QUEUE_FULL;
import static dev.mlnr.spidey.utils.MusicUtils.formatLength;

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
        final var guildId = ctx.getGuild().getIdLong();
        var originalLength = 0L;
        var lengthWithoutSegments = 0L;
        var tracksLoaded = 0;
        for (final var track : tracks)
        {
            final var loadFailure = loadSingle(track, true);
            if (loadFailure == null)
            {
                originalLength += track.getInfo().length;
                lengthWithoutSegments += MusicUtils.getLengthWithoutSegments(track, guildId);
                tracksLoaded++;
                continue;
            }
            if (loadFailure == FAIR_QUEUE)
                continue;
            if (loadFailure == QUEUE_FULL)
            {
                ctx.replyError("I can't add any more tracks as " + QUEUE_FULL.getReason(), Emojis.DISLIKE);
                break;
            }
        }
        if (tracksLoaded == 0)
        {
            ctx.replyError("No tracks could be loaded from the playlist.", Emojis.DISLIKE);
            return;
        }
        ctx.reactLike();
        ctx.reply("**" + tracksLoaded + "** tracks from playlist **" + playlist.getName() + "** " + formatLength(originalLength, lengthWithoutSegments)
                + " have been added to the queue. [" + ctx.getAuthor().getAsMention() + "]", null);
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

    private MusicUtils.LoadFailureReason loadSingle(final AudioTrack track, final boolean playlist)
    {
        final var trackScheduler = musicPlayer.getTrackScheduler();
        final var queue = trackScheduler.getQueue();
        final var trackInfo = track.getInfo();
        final var guildId = ctx.getGuild().getIdLong();
        final var loadFailure = MusicUtils.checkTrack(track, this.musicPlayer, guildId);
        if (loadFailure != null)
        {
            if (!playlist)
                ctx.replyError("I can't load this track as " + loadFailure.getReason(), Emojis.DISLIKE);
            return loadFailure;
        }
        final var requester = ctx.getAuthor();
        final var title = trackInfo.title;
        final var channel = trackInfo.author;
        final var originalLength = trackInfo.length;
        final var stream = trackInfo.isStream;

        MusicUtils.handleMarkers(track, guildId);
        track.setUserData(requester.getIdLong());
        trackScheduler.queue(track, this.insertFirst);

        if (playlist)
            return null;
        final var lengthWithoutSegments = MusicUtils.getLengthWithoutSegments(track, guildId);
        final var responseBuilder = new StringBuilder();
        responseBuilder.append(stream ? "Livestream" : "Track").append(" **").append(title).append("**").append(stream ? "" : " " + formatLength(originalLength, lengthWithoutSegments)).append(" from channel **")
                .append(channel).append("** has ").append(queue.isEmpty() ? "started playing" : "been added to the queue").append(". [").append(requester.getAsMention()).append("]");

        ctx.reactLike();
        ctx.reply(responseBuilder.toString(), null);
        return null;
    }
}