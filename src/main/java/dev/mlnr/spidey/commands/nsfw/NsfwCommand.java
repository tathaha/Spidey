package dev.mlnr.spidey.commands.nsfw;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.nsfw.NSFWType;
import dev.mlnr.spidey.utils.requests.Requester;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;

@SuppressWarnings("unused")
public class NsfwCommand extends Command {
	public NsfwCommand() {
		super("nsfw", "Sends a NSFW image", Category.NSFW, Permission.UNKNOWN, 4,
				new OptionData(OptionType.STRING, "type", "The type of the NSFW image")
						.addChoice(NSFWType.ANAL.getFriendlyName(), NSFWType.ANAL.name())
						.addChoice(NSFWType.ASIANS.getFriendlyName(), NSFWType.ASIANS.name())
						.addChoice(NSFWType.ASS.getFriendlyName(), NSFWType.ASS.name())
						.addChoice(NSFWType.BLOWJOBS.getFriendlyName(), NSFWType.BLOWJOBS.name())
						.addChoice(NSFWType.BOOBS.getFriendlyName(), NSFWType.BOOBS.name())
						.addChoice(NSFWType.CUMSLUTS.getFriendlyName(), NSFWType.CUMSLUTS.name())
						.addChoice(NSFWType.LEGAL_TEENS.getFriendlyName(), NSFWType.LEGAL_TEENS.name())
						.addChoice(NSFWType.LESBIANS.getFriendlyName(), NSFWType.LESBIANS.name())
						.addChoice(NSFWType.MILF.getFriendlyName(), NSFWType.MILF.name())
						.addChoice(NSFWType.NSFW.getFriendlyName(), NSFWType.NSFW.name())
						.addChoice(NSFWType.PUSSY.getFriendlyName(), NSFWType.PUSSY.name())
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