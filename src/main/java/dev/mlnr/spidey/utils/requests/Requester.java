package dev.mlnr.spidey.utils.requests;

import dev.mlnr.spidey.objects.music.VideoSegment;
import dev.mlnr.spidey.utils.requests.api.API;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Float.parseFloat;

public class Requester {
	private static final Logger logger = LoggerFactory.getLogger(Requester.class);
	private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
	private static final Request.Builder REQUEST_BUILDER = new Request.Builder().header("user-agent", "dev.mlnr.spidey");

	private Requester() {}

	public static DataObject executeApiRequest(API api, Object... args) {
		var url = api.getUrl();
		url = String.format(url, args);
		REQUEST_BUILDER.url(url);
		var apiKey = api.getKey();
		if (apiKey != null) {
			REQUEST_BUILDER.header("Authorization", apiKey);
		}
		try (var response = HTTP_CLIENT.newCall(REQUEST_BUILDER.build()).execute(); var body = response.body()) {
			return DataObject.fromJson(body.string());
		}
		catch (Exception ex) {
			logger.error("There was an error while executing a request for url {}:", url, ex);
		}
		return DataObject.empty();
	}

	public static List<VideoSegment> retrieveVideoSegments(String videoId) {
		REQUEST_BUILDER.url("https://sponsor.ajay.app/api/skipSegments?videoID=" + videoId + "&category=music_offtopic");
		try (var response = HTTP_CLIENT.newCall(REQUEST_BUILDER.build()).execute(); var body = response.body()) {
			if (response.code() == 404) {
				return Collections.emptyList();
			}
			var json = DataArray.fromJson(body.string());
			var segments = new ArrayList<VideoSegment>();
			for (var i = 0; i < json.length(); i++) {
				var segmentTimes = json.getObject(i).getArray("segment");
				var segmentStart = (long)(parseFloat(segmentTimes.getString(0)) * 1000);
				var segmentEnd = (long)(parseFloat(segmentTimes.getString(1)) * 1000);
				segments.add(new VideoSegment(segmentStart, segmentEnd));
			}
			return segments;
		}
		catch (Exception ex) {
			logger.error("There was an error while executing a segments request:", ex);
		}
		return Collections.emptyList();
	}
}