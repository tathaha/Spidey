package dev.mlnr.spidey.commands.slash.utility;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;

@SuppressWarnings("unused")
public class EditSnipeSlashCommand extends SlashCommand {
	public EditSnipeSlashCommand() {
		super("editsnipe", "Snipes an edited message", Category.UTILITY, Permission.UNKNOWN, 6);
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