package dev.mlnr.spidey.utils.requests;

import dev.mlnr.spidey.objects.I18n;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.games.VoiceGameType;
import dev.mlnr.spidey.objects.music.VideoSegment;
import dev.mlnr.spidey.utils.Utils;
import dev.mlnr.spidey.utils.requests.api.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Float.parseFloat;

public class Requester {
	private static final Logger logger = LoggerFactory.getLogger(Requester.class);
	private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

	private Requester() {}

	public static void getRandomSubredditImage(String subreddit, CommandContext ctx, GuildMessageReceivedEvent event, I18n i18n, Consumer<EmbedBuilder> embedBuilderConsumer) {
		var requestBuilder = new Request.Builder();
		var url = API.KSOFT.getUrl();
		requestBuilder.url(String.format(url, subreddit, !event.getChannel().isNSFW()));
		requestBuilder.header("Authorization", API.KSOFT.getToken());

		HTTP_CLIENT.newCall(requestBuilder.build()).enqueue(new Callback() {
			@Override
			public void onFailure(final Call call, final IOException e) {
				if (ctx != null) {
					ctx.replyErrorLocalized("internal_error", "get a random image from the subreddit", e.getMessage());
				}
				logger.error("There was an error while executing a request for url {}:", url, e);
			}

			@Override
			public void onResponse(final Call call, final Response response) throws IOException {
				var responseCode = response.code();
				if (responseCode == 404) {
					ctx.replyErrorLocalized("commands.subreddit.other.not_found", subreddit);
					return;
				}
				else if (responseCode == 410) {
					ctx.replyErrorLocalized("commands.subreddit.other.no_posts", subreddit);
					return;
				}
				var responseBody = response.body().string();
				var json = DataObject.fromJson(responseBody);
				var eb = Utils.createEmbedBuilder(event.getAuthor());
				eb.setAuthor(json.getString("title"), json.getString("source"));
				eb.setImage(json.getString("image_url"));
				eb.setDescription(i18n.get("commands.subreddit.other.description", subreddit));
				eb.setTimestamp(Instant.ofEpochSecond(json.getInt("created_at")));
				embedBuilderConsumer.accept(eb);
			}
		});
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

	public static void launchVoiceGameSession(String channelId, VoiceGameType voiceGame, Consumer<String> inviteConsumer, Consumer<Throwable> errorConsumer) {
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
			logger.error("There was an exception while creating an invite for {} for channel {}", voiceGame.getFriendlyName(), channelId, ex);
			errorConsumer.accept(ex);
		}
	}
}