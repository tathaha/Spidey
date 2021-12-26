package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;

@SuppressWarnings("unused")
public class EditSnipeCommand extends Command {
	public EditSnipeCommand() {
		super("editsnipe", "Snipes an edited message", Category.UTILITY, Permission.UNKNOWN, 6, false);
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
		var embedBuilder = Utils.createEmbedBuilder(ctx.getUser());
		embedBuilder.setTimestamp(lastEditedMessage.getCreation());
		embedBuilder.setDescription(lastEditedMessage.getContent());
		embedBuilder.setColor(Color.GREEN);

		ctx.getJDA().retrieveUserById(lastEditedMessage.getAuthorId()).queue(user -> {
			embedBuilder.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());

			var jumpButton = Button.link(lastEditedMessage.getJumpUrl(), ctx.getI18n().get("commands.editsnipe.jump"));
			ctx.replyWithComponents(embedBuilder, jumpButton);
		});
		return true;
	}
}