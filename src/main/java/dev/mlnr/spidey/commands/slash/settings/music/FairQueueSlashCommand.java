package dev.mlnr.spidey.commands.slash.settings.music;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class FairQueueSlashCommand extends SlashCommand {
	public FairQueueSlashCommand() {
		super("fairqueue", "Enables/disables fair queue or sets the threshold", Category.Settings.MUSIC, Permission.UNKNOWN, 4,
				new OptionData(OptionType.BOOLEAN, "enable", "Whether to enable fair queue"),
				new OptionData(OptionType.INTEGER, "threshold", "The fair queue threshold")
						.setRequiredRange(2, 10));
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
		if (!MusicUtils.canInteract(ctx.getMember())) {
			ctx.replyErrorLocalized("music.messages.failure.cant_interact", "enable/disable the fair queue or to set the threshold");
			return false;
		}
		var enabledOption = ctx.getBooleanOption("enable");
		var thresholdOption = ctx.getLongOption("threshold");
		if (enabledOption == null && thresholdOption == null) {
			ctx.replyErrorLocalized("commands.fairqueue.provide_argument");
			return false;
		}
		var guildId = ctx.getGuild().getIdLong();
		var musicSettings = ctx.getCache().getGuildSettingsCache().getMusicSettings(guildId);
		var responseBuilder = new StringBuilder();
		var i18n = ctx.getI18n();
		if (enabledOption != null) {
			var currentState = musicSettings.isFairQueueEnabled();
			if (enabledOption == currentState) {
				ctx.replyErrorLocalized(currentState ? "commands.fairqueue.already_enabled" : "commands.fairqueue.already_disabled");
				return false;
			}
			musicSettings.setFairQueueEnabled(enabledOption);
			responseBuilder.append(i18n.get("commands.fairqueue.done.text", enabledOption ? i18n.get("enabled") : i18n.get("disabled")));
		}
		responseBuilder.append(" ");
		if (thresholdOption != null) {
			var currentThreshold = musicSettings.getFairQueueThreshold();
			if (thresholdOption == currentThreshold) {
				ctx.replyErrorLocalized("commands.fairqueue.already_set", currentThreshold);
				return false;
			}
			var threshold = thresholdOption.intValue();
			musicSettings.setFairQueueThreshold(threshold);
			responseBuilder.append(i18n.get("commands.fairqueue.done.threshold", threshold));
		}
		ctx.reply(responseBuilder.toString());
		return true;
	}
}