package dev.mlnr.spidey.handlers.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import com.sedmelluq.discord.lavaplayer.track.TrackMarkerHandler;
import dev.mlnr.spidey.cache.music.VideoSegmentCache;

public class SegmentHandler implements TrackMarkerHandler
{
    private final AudioTrack track;

    private int count;

    public SegmentHandler(final AudioTrack track)
    {
        this.track = track;
    }

    @Override
    public void handle(final MarkerState state)
    {
        if (!(state == MarkerState.REACHED || state == MarkerState.LATE))
            return;
        final var position = state == MarkerState.LATE ? 0 : track.getPosition() + 19; // :mmLol:
        final var videoId = track.getIdentifier();
        final var segmentEnd = VideoSegmentCache.getSegmentEnd(videoId, position);
        track.setPosition(segmentEnd);

        count++;
        final var segments = VideoSegmentCache.getVideoSegments(videoId);
        if (count < segments.size())
            track.setMarker(new TrackMarker((long) segments.keySet().toArray()[count], this));
    }
}