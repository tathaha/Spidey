package dev.mlnr.spidey.cache.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.objects.music.VideoSegment;
import dev.mlnr.spidey.utils.requests.Requester;

import java.util.Collections;
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

	public List<VideoSegment> getVideoSegments(AudioTrack track) {
		return getVideoSegments(track, false);
	}

	public List<VideoSegment> getVideoSegments(AudioTrack track, boolean forceRequest) {
		if (!track.getSourceManager().getSourceName().equals("youtube")) {
			return Collections.emptyList();
		}
		var videoId = track.getIdentifier();
		if (segmentMap.containsKey(videoId) && !forceRequest) {
			return segmentMap.get(videoId);
		}
		var segments = Requester.retrieveVideoSegments(videoId);
		segmentMap.put(videoId, segments);
		return segments;
	}
}