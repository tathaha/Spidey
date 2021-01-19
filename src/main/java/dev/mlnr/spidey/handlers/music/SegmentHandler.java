package dev.mlnr.spidey.handlers.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import com.sedmelluq.discord.lavaplayer.track.TrackMarkerHandler;
import dev.mlnr.spidey.cache.music.VideoSegmentCache;

public class SegmentHandler implements TrackMarkerHandler
{
    private final AudioTrack track;

    private int currentSegment;

    public SegmentHandler(AudioTrack track)
    {
        this.track = track;
    }

    @Override
    public void handle(MarkerState state)
    {
        if (!(state == MarkerState.REACHED || state == MarkerState.LATE))
            return;
        var segments = VideoSegmentCache.getVideoSegments(track.getIdentifier());
        track.setPosition(segments.get(this.currentSegment).getSegmentEnd());

        this.currentSegment++;
        if (this.currentSegment < segments.size())
            track.setMarker(new TrackMarker(segments.get(this.currentSegment).getSegmentStart(), this));
    }
}