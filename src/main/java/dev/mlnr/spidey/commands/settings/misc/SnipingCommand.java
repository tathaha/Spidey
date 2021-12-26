package dev.mlnr.spidey.commands.settings.misc;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@SuppressWarnings("unused")
public class SnipingCommand extends Command {
	public SnipingCommand() {
		super("sniping", "Enables/disables message delete/edit sniping", Category.Settings.MISC, Permission.MANAGE_SERVER, 4,
				Utils.createConvenientOption(OptionType.BOOLEAN, "enable", "Whether to enable (edit)sniping", true));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var cache = ctx.getCache();
		var guildId = ctx.getGuild().getIdLong();
		var miscSettings = cache.getGuildSettingsCache().getMiscSettings(guildId);
		var enabled = ctx.getBooleanOption("enable");
		var i18n = ctx.getI18n();
		miscSettings.setSnipingEnabled(enabled);
		ctx.replyLocalized("commands.sniping.done", enabled ? i18n.get("enabled") : i18n.get("disabled"));
		if (!enabled) {
			cache.getMessageCache().pruneCache(guildId);
		}
		return true;
	}
}