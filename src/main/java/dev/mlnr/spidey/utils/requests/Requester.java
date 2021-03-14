package dev.mlnr.spidey.utils.requests;

import dev.mlnr.spidey.objects.activities.VoiceGameType;
import dev.mlnr.spidey.objects.music.VideoSegment;
import dev.mlnr.spidey.utils.requests.api.API;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Float.parseFloat;

public class Requester {
	private static final Logger logger = LoggerFactory.getLogger(Requester.class);
	private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

	private Requester() {}

	public static DataObject executeApiRequest(API api, Object... args) {
		var requestBuilder = new Request.Builder();
		var url = String.format(api.getUrl(), args);
		requestBuilder.url(url);
		var apiKey = api.getKey();
		if (apiKey != null) {
			requestBuilder.header("Authorization", apiKey);
		}
		try (var response = HTTP_CLIENT.newCall(requestBuilder.build()).execute(); var body = response.body()) {
			return DataObject.fromJson(body.string());
		}
		catch (Exception ex) {
			logger.error("There was an error while executing a request for url {}:", url, ex);
		}
		return DataObject.empty();
	}

	public static List<VideoSegment> retrieveVideoSegments(String videoId) {
		var requestBuilder = new Request.Builder();
		requestBuilder.url("https://sponsor.ajay.app/api/skipSegments?videoID=" + videoId + "&category=music_offtopic");
		try (var response = HTTP_CLIENT.newCall(requestBuilder.build()).execute(); var body = response.body()) {
			if (response.code() == 404) {
				return Collections.emptyList();
			}
			var json = DataArray.fromJson(body.string());
			var segments = new ArrayList<VideoSegment>();
			for (var i = 0; i < json.length(); i++) {
				var segmentTimes = json.getObject(i).getArray("segment");
				var segmentStart = (long) (parseFloat(segmentTimes.getString(0)) * 1000);
				var segmentEnd = (long) (parseFloat(segmentTimes.getString(1)) * 1000);
				segments.add(new VideoSegment(segmentStart, segmentEnd));
			}
			return segments;
		}
		catch (Exception ex) {
			logger.error("There was an error while executing a segments request:", ex);
		}
		return Collections.emptyList();
	}

	public static void launchYouTubeTogetherSession(String channelId, VoiceGameType voiceGame, Consumer<String> inviteConsumer, Consumer<Throwable> errorConsumer) {
		var payload = DataObject.empty().put("max_age", 0).put("target_type", 2).put("target_application_id", voiceGame.getApplicationId());
		var requestBody = RequestBody.create(MediaType.parse("application/json"), payload.toString());
		var requestBuilder = new Request.Builder()
				.header("Authorization", "Bot " + System.getenv("Spidey"))
				.url("https://discord.com/api/v8/channels/" + channelId + "/invites")
				.post(requestBody);
		try (var response = HTTP_CLIENT.newCall(requestBuilder.build()).execute(); var body = response.body()) {
			var json = DataObject.fromJson(body.string());
			inviteConsumer.accept(json.getString("code"));
		}
		catch (Exception ex) {
			logger.error("There was an exception while launching a {} session for channel {}", voiceGame.getFriendlyName(), channelId, ex);
			errorConsumer.accept(ex);
		}
	}
}