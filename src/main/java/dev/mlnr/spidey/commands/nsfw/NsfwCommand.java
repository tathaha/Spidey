package dev.mlnr.spidey.commands.nsfw;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.nsfw.NSFWType;
import dev.mlnr.spidey.objects.nsfw.PostSpan;
import dev.mlnr.spidey.utils.Utils;
import dev.mlnr.spidey.utils.requests.Requester;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;

@SuppressWarnings("unused")
public class NsfwCommand extends Command {
	public NsfwCommand() {
		super("nsfw", "Sends a NSFW image", Category.NSFW, Permission.UNKNOWN, 4,
				new OptionData(OptionType.STRING, "type", "The type of the NSFW image", true)
						.addChoices(Utils.getChoicesFromEnum(NSFWType.class)),
				new OptionData(OptionType.STRING, "span", "The timespan to get the post from or blank to choose month")
						.addChoices(Utils.getChoicesFromEnum(PostSpan.class)));
		withFlags(Command.Flags.NO_THREADS);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var channel = ctx.getTextChannel();
		if (!channel.isNSFW()) {
			ctx.replyErrorLocalized("command_failures.only_nsfw");
			return false;
		}
		var type = NSFWType.valueOf(ctx.getStringOption("type"));
		var span = ctx.getStringOption("span");
		Requester.getRandomSubredditImage(type.getSubreddit(), span, ctx, embedBuilder -> {
			embedBuilder.setColor(Color.PINK);
			ctx.reply(embedBuilder);
		});
		return true;
	}
}