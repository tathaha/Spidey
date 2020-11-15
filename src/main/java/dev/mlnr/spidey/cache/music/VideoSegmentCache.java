package dev.mlnr.spidey.cache.music;

import dev.mlnr.spidey.objects.music.VideoSegment;
import dev.mlnr.spidey.utils.requests.Requester;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoSegmentCache
{
    private static final Map<String, List<VideoSegment>> SEGMENT_CACHE = new HashMap<>();

    private VideoSegmentCache() {}

    public static List<VideoSegment> getVideoSegments(final String videoId)
    {
        return getVideoSegments(videoId, false);
    }

    public static List<VideoSegment> getVideoSegments(final String videoId, final boolean forceRequest)
    {
        if (SEGMENT_CACHE.containsKey(videoId) && !forceRequest)
            return SEGMENT_CACHE.get(videoId);
        final var segments = Requester.retrieveVideoSegments(videoId);
        SEGMENT_CACHE.put(videoId, segments);
        return segments;
    }
}