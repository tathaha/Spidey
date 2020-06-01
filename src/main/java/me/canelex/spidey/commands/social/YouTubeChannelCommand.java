package me.canelex.spidey.commands.social;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import me.canelex.spidey.Secrets;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.objects.json.SocialBlade;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static java.time.OffsetDateTime.parse;
import static java.util.List.of;
import static me.canelex.spidey.utils.Utils.format;
import static me.canelex.spidey.utils.Utils.getCompactNumber;

@SuppressWarnings("unused")
public class YouTubeChannelCommand extends Command
{
	private static final Logger LOG = LoggerFactory.getLogger(YouTubeChannelCommand.class);

	public YouTubeChannelCommand()
	{
		super("ytchannel", new String[]{}, "Shows info about entered YouTube channel", "ytchannel <channel/id>",
				Category.SOCIAL, Permission.UNKNOWN, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		if (args.length == 0)
		{
			Utils.returnError("Please specify a YouTube channel", message);
			return;
		}
		final var channel = message.getChannel();
		try
		{
			final var youtube = new YouTube.Builder(
					new NetHttpTransport(),
					new JacksonFactory(),
					request -> {})
					.setApplicationName("youtube-4HEad")
					.setYouTubeRequestInitializer(new YouTubeRequestInitializer(Secrets.YOUTUBEAPIKEY))
					.build();

			final var search = youtube.search().list(of("snippet")).setQ(args[0]).setType(of("channel"));
			final var searchResponse = search.execute();

			if (!searchResponse.getItems().isEmpty())
			{
				final var eb = Utils.createEmbedBuilder(message.getAuthor());
				channel.sendMessage("Fetching data...")
					   .delay(Duration.ofSeconds(4))
					   .flatMap(msg -> msg.editMessage(eb.build()).override(true))
					   .queue();

				final var channelId = searchResponse.getItems().get(0).getSnippet().getChannelId();
				final var channels = youtube.channels().list(of("snippet, statistics")).setId(of(channelId));
				final var c = channels.execute().getItems().get(0);
				final var creation = Utils.getTime(parse(c.getSnippet().getPublishedAt()).toInstant().toEpochMilli());
				final var sb = new SocialBlade(channelId);
				final var subs = sb.getSubs();
				final var views = sb.getViews();
				eb.setAuthor(c.getSnippet().getTitle(), "https://youtube.com/channel/" + channelId, "https://up.mlnr.dev/yt.png");
				eb.setColor(14765121);
				eb.setThumbnail(sb.getAvatar());
				eb.addField("Subscribers", (subs >= 1000 ? "**" + getCompactNumber(subs) + "** (**" + format(subs) + "**)" : "**" + subs + "**"), false);
				eb.addField("Views", (views >= 1000 ? "**" + getCompactNumber(views) + "** (**" + format(views) + "**)" : "**" + views + "**"), false);
				eb.addField("Videos", "**" + sb.getVideos() + "**", false);
				eb.addField("Created", "**" + creation + "**", false);
				eb.addField("Partner", (sb.isPartner() ? "**Yes**" : "**No**"), false);
				eb.addField("Verified", (sb.isVerified() ? "**Yes**" : "**No**"), false);
				eb.addField("Country", "**" + sb.getCountry() + "**", false);
				final var latestVideo = Utils.getSiteContent("https://beta.decapi.me/youtube/latest_video/?id=" + channelId);
				eb.addField("Latest video", (latestVideo.equals("An error occurred retrieving videos for channel: " + channelId) ? "**This channel has no videos**" : latestVideo), false);

				if (!sb.getBanner().equals("http://s.ytimg.com/yts/img/channels/c4/default_banner-vfl7DRgTn.png"))
				{
					eb.addBlankField(false);
					eb.setImage(sb.getBanner());
				}
			}
			else
				Utils.sendMessage(channel, ":no_entry: No results found.");
		}
		catch (final Exception ex)
		{
			LOG.error("There was an error requesting channel {}!", channel, ex);
		}
	}
}