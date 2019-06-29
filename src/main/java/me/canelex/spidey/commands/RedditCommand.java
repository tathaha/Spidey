package me.canelex.spidey.commands;

import me.canelex.spidey.objects.category.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.objects.json.Reddit;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class RedditCommand implements ICommand {

	private static final Logger logger = LoggerFactory.getLogger(RedditCommand.class);

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final String subreddit = e.getMessage().getContentRaw().substring(9);

		try {

			final Reddit reddit = new Reddit().getSubReddit(subreddit);

			if (reddit == null) {
				e.getChannel().sendMessage(":no_entry: Subreddit not found.").queue(m -> {
					m.delete().queueAfter(5, TimeUnit.SECONDS);
					e.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
				});
				return;
			}

			final EmbedBuilder eb = Utils.createEmbedBuilder(e.getAuthor());
			final String comIcon = reddit.getCommunityIcon().length() == 0 ? "https://i.ymastersk.net/LRjhvy" : reddit.getCommunityIcon();
			eb.setAuthor("r/" + reddit.getName(), "https://reddit.com/r/" + subreddit, "https://i.ymastersk.net/LRjhvy");
			eb.setThumbnail(reddit.getIcon().length() == 0 ? comIcon : reddit.getIcon());
			eb.setColor(16727832);
			eb.addField("Subscribers", "**" + reddit.getSubs() + "**", false);
			eb.addField("Active users", "**" + reddit.getActive() + "**", false);
			eb.addField("Title", (reddit.getTitle().length() == 0 ? "**None**" : reddit.getTitle()), false);
			eb.addField("Description", (reddit.getDesc().length() == 0 ? "**None**" : reddit.getDesc()), false);
			eb.addField("NSFW", "**" + (reddit.isNsfw() ? "Yes" : "No") + "**", false);

			Utils.sendMessage(e.getChannel(), eb.build());

		} catch (final IOException ex) {
			logger.error("Exception!", ex);
		}

	}

	@Override
	public final String getDescription() { return "Shows you info about entered subreddit. For example `s!reddit PewdiepieSubmissions`."; }
	@Override
	public final boolean isAdmin() { return false; }
	@Override
	public final String getInvoke() { return "reddit"; }
	@Override
	public final Category getCategory() { return Category.MISC; }
	@Override
	public final String getUsage() { return "s!reddit <subreddit>"; }

}