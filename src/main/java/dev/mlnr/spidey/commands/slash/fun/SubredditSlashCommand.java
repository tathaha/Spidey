package dev.mlnr.spidey.commands.slash.fun;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.objects.nsfw.PostSpan;
import dev.mlnr.spidey.utils.CommandUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class SubredditSlashCommand extends SlashCommand {
	public SubredditSlashCommand() {
		super("subreddit", "Sends a random image from given subreddit in the given timespan", Category.FUN, Permission.UNKNOWN, 3,
				new OptionData(OptionType.STRING, "subreddit", "The subreddit to get a random image from", true),
				new OptionData(OptionType.STRING, "span", "The timespan to get the post from or blank to choose month")
						.addChoices(CommandUtils.getChoicesFromEnum(PostSpan.class)));
		withFlags(SlashCommand.Flags.NO_THREADS);
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
		ctx.replyErrorLocalized("unavailable");
//		Requester.getRandomSubredditImage(ctx.getStringOption("subreddit"), ctx.getStringOption("span"), ctx, ctx::reply);
		return true;
	}
}