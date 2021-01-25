package dev.mlnr.spidey.cache.music;

import dev.mlnr.spidey.objects.music.VideoSegment;
import dev.mlnr.spidey.utils.requests.Requester;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoSegmentCache {
	private final Map<String, List<VideoSegment>> segmentMap = new HashMap<>();

	private static VideoSegmentCache videoSegmentCache;

	public static synchronized VideoSegmentCache getInstance() {
		if (videoSegmentCache == null)
			videoSegmentCache = new VideoSegmentCache();
		return videoSegmentCache;
	}

	public List<VideoSegment> getVideoSegments(String videoId) {
		return getVideoSegments(videoId, false);
	}

	public List<VideoSegment> getVideoSegments(String videoId, boolean forceRequest) {
		if (segmentMap.containsKey(videoId) && !forceRequest) {
			return segmentMap.get(videoId);
		}
		var segments = Requester.retrieveVideoSegments(videoId);
		segmentMap.put(videoId, segments);
		return segments;
	}
}