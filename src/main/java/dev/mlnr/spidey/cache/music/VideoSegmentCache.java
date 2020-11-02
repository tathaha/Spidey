package dev.mlnr.spidey.cache.music;

import dev.mlnr.spidey.utils.requests.Requester;

import java.util.LinkedHashMap;
import java.util.Map;

public class VideoSegmentCache
{
    private static final Map<String, Map<Long, Long>> SEGMENT_CACHE = new LinkedHashMap<>();

    private VideoSegmentCache() {}

    public static Map<Long, Long> getVideoSegments(final String videoId)
    {
        return getVideoSegments(videoId, false);
    }

    public static Map<Long, Long> getVideoSegments(final String videoId, final boolean forceRequest)
    {
        if (SEGMENT_CACHE.containsKey(videoId) && !forceRequest)
            return SEGMENT_CACHE.get(videoId);
        final var segments = Requester.retrieveVideoSegments(videoId);
        SEGMENT_CACHE.put(videoId, segments);
        return segments;
    }

    public static long getSegmentEnd(final String videoId, final long segmentStart)
    {
        final var segments = SEGMENT_CACHE.get(videoId);
        final var segmentEnd = segments.get(segmentStart);
        return segmentEnd != null ? segmentEnd : segments.get(segmentStart + 1); // :mmLol:
    }
}