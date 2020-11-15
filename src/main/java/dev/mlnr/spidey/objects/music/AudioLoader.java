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
        final var guildId = ctx.getGuild().getIdLong();
        var originalLength = 0L;
        var durationWithoutSegments = 0L;
        var tracksLoaded = 0;
        var overLengthLimit = 0;
        var overFairQueueLimit = 0;
        for (final var track : tracks)
        {
            final var loadFailure = loadSingle(track, true);
            if (loadFailure == null)
            {
                originalLength += track.getInfo().length;
                durationWithoutSegments += MusicUtils.getLengthWithoutSegments(track, guildId);
                tracksLoaded++;
                continue;
            }
            if (loadFailure == FAIR_QUEUE)
            {
                overFairQueueLimit++;
                continue;
            }
            if (loadFailure == QUEUE_FULL)
            {
                ctx.replyError("I can't add any more tracks as " + QUEUE_FULL.getReason(), Emojis.DISLIKE);
                break;
            }
            overLengthLimit++; // the track is over 2 hours long
        }
        if (tracksLoaded == 0)
        {
            ctx.replyError("No tracks could be loaded from the playlist. ", Emojis.DISLIKE);
            return;
        }
        final var duration = "(**" + formatDuration(originalLength) + "**" + (durationWithoutSegments == originalLength ? "" : " [**" + formatDuration(durationWithoutSegments) + "** without segments]") + ")";
        final var trackAmount = tracks.size();
        final var template = "**" + tracksLoaded + "** tracks from playlist **" + playlist.getName() + "** " + duration + " have been added to the queue. ";
        if (tracksLoaded == trackAmount)
        {
            ctx.reactLike();
            ctx.reply(template + "[" + ctx.getAuthor().getAsMention() + "]", null);
            return;
        }
        if (overFairQueueLimit == trackAmount)
        {
            ctx.replyError("All tracks from the playlist were over the fair queue limit", Emojis.DISLIKE);
            return;
        }
        if (overLengthLimit == trackAmount)
            ctx.replyError("All tracks from the playlist were longer than 2 hours. To completely remove the length limit, you can purchase permanent VIP for a symbolic price of 2€", Emojis.DISLIKE);
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
        final var duration = "(**" + formatDuration(originalLength) + "**" + (lengthWithoutSegments == originalLength ? "" : " [**" + formatDuration(lengthWithoutSegments) + "** without segments]") + ")";

        ctx.reactLike();
        ctx.reply((stream ? "Livestream" : "Track") + " **" + title + "**" + (stream ? "" : " " + duration) + " from channel **" + channel + "**" + " has " + (queue.isEmpty()
                ? "started playing" : "been added to the queue") + ". [" + requester.getAsMention() + "]", null);
        return null;
    }
}