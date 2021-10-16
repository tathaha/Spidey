package dev.mlnr.spidey.utils.requests;

import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.games.VoiceGameType;
import dev.mlnr.spidey.objects.music.VideoSegment;
import dev.mlnr.spidey.utils.Utils;
import dev.mlnr.spidey.utils.requests.api.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
	private static final String SPONSORBLOCK_URL = "https://sponsor.ajay.app/api/skipSegments?videoID=%s&categories=[\"sponsor\", \"selfpromo\", \"interaction\", \"intro\", \"outro\", \"preview\", \"music_offtopic\"]";

	private Requester() {}

	public static void getRandomSubredditImage(String subreddit, String span, CommandContext ctx, Consumer<EmbedBuilder> embedBuilderConsumer) {
		var requestBuilder = new Request.Builder();
		var url = API.KSOFT.getUrl();
		var event = ctx.getEvent();
		requestBuilder.url(String.format(url, subreddit, span == null ? "month": span.toLowerCase(), !event.getTextChannel().isNSFW()));
		requestBuilder.header("Authorization", API.KSOFT.getToken());

		HTTP_CLIENT.newCall(requestBuilder.build()).enqueue(new Callback() {
			@Override
			public void onFailure(final Call call, final IOException e) {
				ctx.replyErrorLocalized("internal_error", "get a random image from the subreddit", e.getMessage());
				logger.error("There was an error while executing a request for url {}:", url, e);
			}

			@Override
			public void onResponse(final Call call, final Response response) throws IOException {
				var responseCode = response.code();
				if (responseCode == 404) {
					ctx.replyErrorLocalized("commands.subreddit.not_found", subreddit);
					return;
				}
				else if (responseCode == 410) {
					ctx.replyErrorLocalized("commands.subreddit.no_posts", subreddit);
					return;
				}
				var body = response.body();
				var responseBody = body.string();
				var json = DataObject.fromJson(responseBody);
				var embedBuilder = Utils.createEmbedBuilder(event.getUser());
				embedBuilder.setAuthor(json.getString("title"), json.getString("source"));
				embedBuilder.setImage(json.getString("image_url"));
				embedBuilder.setDescription(ctx.getI18n().get("commands.subreddit.description", subreddit));
				embedBuilder.setTimestamp(Instant.ofEpochSecond(json.getInt("created_at")));
				embedBuilderConsumer.accept(embedBuilder);
				body.close();
			}
		});
	}

	public static List<VideoSegment> retrieveVideoSegments(String videoId) {
		var requestBuilder = new Request.Builder();
		requestBuilder.url(String.format(SPONSORBLOCK_URL, videoId));
		try (var response = HTTP_CLIENT.newCall(requestBuilder.build()).execute(); var body = response.body()) {
			if (!response.isSuccessful()) {
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

	public static void launchVoiceGameSession(GuildChannel channel, VoiceGameType voiceGame, Consumer<String> inviteConsumer, Consumer<Throwable> errorConsumer) {
		var channelId = channel.getId();
		var route = Route.Invites.CREATE_INVITE.compile(channelId);
		var payload = DataObject.empty().put("max_age", 0).put("target_type", 2).put("target_application_id", voiceGame.getApplicationId());
		var createInvite = new RestActionImpl<String>(channel.getJDA(), route, payload, (response, request) -> response.getObject().getString("code"));
		createInvite.queue(inviteConsumer, failure -> {
			logger.error("There was an exception while creating an invite for {} for channel {}", voiceGame.getFriendlyName(), channelId, failure);
			errorConsumer.accept(failure);
		});
	}
}