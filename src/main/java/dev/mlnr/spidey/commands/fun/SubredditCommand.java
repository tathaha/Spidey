package dev.mlnr.spidey.commands.fun;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.requests.Requester;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class SubredditCommand extends Command {
	public SubredditCommand() {
		super("subreddit", new String[]{"sub", "reddit"}, Category.FUN, Permission.UNKNOWN, 0, 3);
	}

	@Override
	public boolean execute(String[] args, CommandContext ctx) {
		if (args.length == 0) {
			ctx.replyErrorLocalized("commands.subreddit.other.enter_name");
			return false;
		}
		Requester.getRandomSubredditImage(args[0], ctx, ctx.getEvent(), ctx.getI18n(), ctx::reply);
		return true;
	}
}