package dev.mlnr.spidey.commands.nsfw;

import dev.mlnr.spidey.objects.command.CommandBase;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.nsfw.NSFWType;
import dev.mlnr.spidey.utils.requests.Requester;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class NsfwCommand extends CommandBase {
	public NsfwCommand() {
		super("nsfw", "Sends a NSFW image", Category.NSFW, Permission.UNKNOWN, 4,
				new OptionData(OptionType.STRING, "type", "The type of the NSFW image")
						.addChoices(Arrays.stream(NSFWType.values()).map(
								nsfwType -> new Command.Choice(nsfwType.getFriendlyName(), nsfwType.name())).collect(Collectors.toList()))
						.setRequired(true));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var channel = ctx.getTextChannel();
		if (!channel.isNSFW()) {
			ctx.replyErrorLocalized("command_failures.only_nsfw");
			return false;
		}
		var type = NSFWType.valueOf(ctx.getStringOption("type"));
		Requester.getRandomSubredditImage(type.getSubreddit(), ctx, embedBuilder -> {
			embedBuilder.setColor(Color.PINK);
			ctx.reply(embedBuilder);
		});
		return true;
	}
}