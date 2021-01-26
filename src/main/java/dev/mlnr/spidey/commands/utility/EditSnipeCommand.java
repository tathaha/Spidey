package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.awt.*;

@SuppressWarnings("unused")
public class EditSnipeCommand extends Command {

	public EditSnipeCommand() {
		super("editsnipe", new String[]{"esnipe", "es"}, Category.UTILITY, Permission.UNKNOWN, 0, 6);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var cache = ctx.getCache();
		var guildSettingsCache = cache.getGuildSettingsCache();
		var guildId = ctx.getGuild().getIdLong();
		var i18n = ctx.getI18n();
		if (!guildSettingsCache.isSnipingEnabled(guildId)) {
			ctx.replyError(i18n.get("sniping.disabled", guildSettingsCache.getPrefix(guildId)));
			return;
		}
		var textChannel = ctx.getTextChannel();
		var channelId = textChannel.getIdLong();
		var lastEditedMessage = cache.getMessageCache().getLastEditedMessage(channelId);
		if (lastEditedMessage == null) {
			ctx.replyError(i18n.get("sniping.no_message", "edited"));
			return;
		}
		var eb = Utils.createEmbedBuilder(ctx.getAuthor());
		eb.setTimestamp(lastEditedMessage.getCreation());
		eb.setDescription(lastEditedMessage.getContent());
		eb.setColor(Color.GREEN);

		ctx.getJDA().retrieveUserById(lastEditedMessage.getAuthorId()).queue(user -> {
			eb.setAuthor(user.getName(), lastEditedMessage.getJumpUrl(), user.getEffectiveAvatarUrl());
			ctx.reply(eb);
		});
	}
}