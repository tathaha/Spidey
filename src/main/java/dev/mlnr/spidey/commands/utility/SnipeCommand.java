package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.awt.*;

@SuppressWarnings("unused")
public class SnipeCommand extends Command {
	public SnipeCommand() {
		super("snipe", "Snipes a deleted message", Category.UTILITY, Permission.UNKNOWN, 6, false);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var cache = ctx.getCache();
		var miscSettings = cache.getGuildSettingsCache().getMiscSettings(ctx.getGuild().getIdLong());
		if (!miscSettings.isSnipingEnabled()) {
			ctx.replyErrorLocalized("sniping.disabled");
			return false;
		}
		var textChannel = ctx.getTextChannel();
		var channelId = textChannel.getIdLong();
		var lastDeletedMessage = cache.getMessageCache().getLastDeletedMessage(channelId);
		if (lastDeletedMessage == null) {
			ctx.replyErrorLocalized("sniping.no_message", "deleted");
			return false;
		}
		var embedBuilder = Utils.createEmbedBuilder(ctx.getUser());
		embedBuilder.setTimestamp(lastDeletedMessage.getCreation());
		embedBuilder.setDescription(lastDeletedMessage.getContent());
		embedBuilder.setColor(Color.GREEN);

		ctx.getJDA().retrieveUserById(lastDeletedMessage.getAuthorId()).queue(user -> {
			embedBuilder.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
			ctx.reply(embedBuilder);
		});
		return true;
	}
}