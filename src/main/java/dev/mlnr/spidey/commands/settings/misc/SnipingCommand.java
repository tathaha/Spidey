package dev.mlnr.spidey.commands.settings.misc;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class SnipingCommand extends Command {

	public SnipingCommand() {
		super("sniping", new String[]{}, Category.Settings.MISC, Permission.MANAGE_SERVER, 0, 4);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var cache = ctx.getCache();
		var guildId = ctx.getGuild().getIdLong();
		var miscSettings = cache.getGuildSettingsCache().getMiscSettings(guildId);
		var enabled = !miscSettings.isSnipingEnabled();
		var i18n = ctx.getI18n();
		miscSettings.setSnipingEnabled(enabled);
		ctx.reactLike();
		ctx.replyLocalized("commands.sniping.other.done", enabled ? i18n.get("enabled") : i18n.get("disabled"));
		if (!enabled) {
			cache.getMessageCache().pruneCache(guildId);
		}
		return true;
	}
}