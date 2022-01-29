package dev.mlnr.spidey.commands.slash.nsfw;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.objects.nsfw.NSFWType;
import dev.mlnr.spidey.objects.nsfw.PostSpan;
import dev.mlnr.spidey.utils.CommandUtils;
import dev.mlnr.spidey.utils.requests.Requester;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;

@SuppressWarnings("unused")
public class NsfwSlashCommand extends SlashCommand {
	public NsfwSlashCommand() {
		super("nsfw", "Sends a NSFW image", Category.NSFW, Permission.UNKNOWN, 4,
				new OptionData(OptionType.STRING, "type", "The type of the NSFW image", true)
						.addChoices(CommandUtils.getChoicesFromEnum(NSFWType.class)),
				new OptionData(OptionType.STRING, "span", "The timespan to get the post from or blank to choose month")
						.addChoices(CommandUtils.getChoicesFromEnum(PostSpan.class)));
		withFlags(SlashCommand.Flags.NO_THREADS);
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
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