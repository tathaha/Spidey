package me.canelex.spidey.commands.social;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Message;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.objects.json.Reddit;
import me.canelex.spidey.utils.Utils;

@SuppressWarnings("unused")
public class RedditCommand extends Command
{
	public RedditCommand()
	{
		super("reddit", new String[]{}, "Shows you info about entered subreddit. For example `reddit PewdiepieSubmissions`.",
				"reddit <subreddit>", Category.SOCIAL, Permission.UNKNOWN, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var subreddit = message.getContentRaw().substring(9);
		final var reddit = new Reddit(subreddit);
		if (!reddit.exists())
		{
			Utils.returnError("Subreddit not found", message);
			return;
		}
		final var eb = Utils.createEmbedBuilder(message.getAuthor());
		final var communityIcon = reddit.getCommunityIcon();
		final var comIcon = communityIcon.length() == 0 ? "https://up.mlnr.dev/reddit.png" : communityIcon;
		final var icon = reddit.getIcon();
		final var title = reddit.getTitle();
		final var desc = reddit.getDesc();
		eb.setAuthor("r/" + reddit.getName(), "https://reddit.com/r/" + subreddit, "https://up.mlnr.dev/reddit.png");
		eb.setThumbnail(icon.length() == 0 ? comIcon : icon);
		eb.setColor(16727832);
		eb.addField("Subscribers", "**" + reddit.getSubs() + "**", false);
		eb.addField("Active users", "**" + reddit.getActive() + "**", false);
		eb.addField("Title", (title.length() == 0 ? "**None**" : title), false);
		eb.addField("Description", (desc.length() == 0 ? "**None**" : desc), false);
		eb.addField("NSFW", "**" + (reddit.isNsfw() ? "Yes" : "No") + "**", false);
		Utils.sendMessage(message.getChannel(), eb.build());
	}
}