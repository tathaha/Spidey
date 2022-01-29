package dev.mlnr.spidey.commands.slash.utility;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.awt.*;

@SuppressWarnings("unused")
public class SnipeSlashCommand extends SlashCommand {
	public SnipeSlashCommand() {
		super("snipe", "Snipes a deleted message", Category.UTILITY, Permission.UNKNOWN, 6);
		withFlags(SlashCommand.Flags.SHOW_RESPONSE);
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
		var cache = ctx.getCache();
		var miscSettings = cache.getGuildSettingsCache().getMiscSettings(ctx.getGuild().getIdLong());
		if (!miscSettings.isSnipingEnabled()) {
			ctx.replyErrorLocalized("sniping.disabled");
			return false;
		}
		var textChannel = ctx.getChannel();
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