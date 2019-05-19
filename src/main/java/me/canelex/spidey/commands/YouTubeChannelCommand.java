package me.canelex.spidey.commands;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.SearchListResponse;
import com.mashape.unirest.http.Unirest;
import me.canelex.spidey.Secrets;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.objects.json.SocialBlade;
import me.canelex.spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class YouTubeChannelCommand implements ICommand {

	private final Locale locale = new Locale("en", "EN");
	private final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
	private final SimpleDateFormat date = new SimpleDateFormat("EEEE, d.LLLL Y", locale);
	private final SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", locale);

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final String channel = e.getMessage().getContentRaw().substring(12);
		final Message msg = e.getChannel().sendMessage("Fetching data..").complete(); //TODO temporary solution

		try {

			final YouTube youtube = new YouTube.Builder(
					new NetHttpTransport(),
					new JacksonFactory(),
					request -> {})
					.setApplicationName("youtube-cmdline-search-sample")
					.setYouTubeRequestInitializer(new YouTubeRequestInitializer(Secrets.YOUTUBEAPIKEY))
					.build();

			final YouTube.Search.List search = youtube.search().list("snippet");
			search.setQ(channel);
			search.setType("channel");

			final SearchListResponse searchResponse = search.execute();

			if (!searchResponse.getItems().isEmpty()) {

				final String channelId = searchResponse.getItems().get(0).getSnippet().getChannelId();
				final YouTube.Channels.List channels = youtube.channels().list("snippet, statistics");
				channels.setId(channelId);
				final Channel c = channels.execute().getItems().get(0);

				cal.setTimeInMillis(c.getSnippet().getPublishedAt().getValue());

				final String creatdate = date.format(cal.getTime());
				final String creattime = time.format(cal.getTime());

				final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
				final SocialBlade sb = new SocialBlade().getYouTube(channelId);
				eb.setAuthor(c.getSnippet().getTitle(), "https://youtube.com/channel/" + channelId, "https://i.ymastersk.net/vo96zG");
				eb.setColor(14765121);
				eb.setThumbnail(sb.getAvatar());
				eb.addField("Subscribers", "**" + sb.getSubs() + "**", false);
				eb.addField("Views", "**" + sb.getViews() + "**", false);
				eb.addField("Videos", "**" + sb.getVideos() + "**", false);
				eb.addField("Created", String.format( "**%s** | **%s** UTC", creatdate, creattime), false);
				eb.addField("Partner", (sb.isPartner() ? "**Yes**" : "**No**"), false);
				eb.addField("Verified", (sb.isVerified() ? "**Yes**" : "**No**"), false);
				eb.addField("Country", "**" + sb.getCountry() + "**", false);
				final String latestVideo = Unirest.get("https://beta.decapi.me/youtube/latest_video/?id=" + channelId).asString().getBody();
				eb.addField("Latest video", (latestVideo.equals("An error occurred retrieving videos for channel: " + channelId) ? "**This channel has no videos**" : latestVideo), false);

				if (!sb.getBanner().equals("http://s.ytimg.com/yts/img/channels/c4/default_banner-vfl7DRgTn.png")) {
					eb.addBlankField(false);
					eb.setImage(sb.getBanner());
				}

				msg.editMessage(eb.build()).queue();

			}

			else {

				API.sendMessage(e.getChannel(), ":no_entry: No results found.", false);

			}

		}

		catch (final Exception ex) {
			LoggerFactory.getLogger(YouTubeChannelCommand.class).error("Exception!", ex);
		}

	}

	@Override
	public final String help() {

		return "Shows info about entered YouTube channel";

	}

	@Override
	public final boolean isAdmin() {
		return false;
	}

}