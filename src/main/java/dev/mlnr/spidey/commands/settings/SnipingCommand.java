package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class SnipingCommand extends Command {

	public SnipingCommand() {
		super("sniping", new String[]{}, Category.SETTINGS, Permission.MANAGE_SERVER, 0, 4);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var cache = ctx.getCache();
		var guildSettingsCache = cache.getGuildSettingsCache();
		var guildId = ctx.getGuild().getIdLong();
		var enabled = !guildSettingsCache.isSnipingEnabled(guildId);
		var i18n = ctx.getI18n();
		guildSettingsCache.setSnipingEnabled(guildId, enabled);
		ctx.reactLike();
		ctx.reply(i18n.get("commands.sniping.other.done", enabled ? i18n.get("enabled") : i18n.get("disabled")));
		if (!enabled) {
			cache.getMessageCache().pruneCache(guildId);
		}
	}
}