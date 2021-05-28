package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.awt.*;

@SuppressWarnings("unused")
public class EditSnipeCommand extends Command {
	public EditSnipeCommand() {
		super("editsnipe", Category.UTILITY, Permission.UNKNOWN, 6);
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
		var lastEditedMessage = cache.getMessageCache().getLastEditedMessage(channelId);
		if (lastEditedMessage == null) {
			ctx.replyErrorLocalized("sniping.no_message", "edited");
			return false;
		}
		var eb = Utils.createEmbedBuilder(ctx.getUser());
		eb.setTimestamp(lastEditedMessage.getCreation());
		eb.setDescription(lastEditedMessage.getContent());
		eb.setColor(Color.GREEN);

		ctx.getJDA().retrieveUserById(lastEditedMessage.getAuthorId()).queue(user -> {
			eb.setAuthor(user.getName(), lastEditedMessage.getJumpUrl(), user.getEffectiveAvatarUrl());
			ctx.reply(eb);
		});
		return true;
	}
}