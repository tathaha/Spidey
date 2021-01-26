package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class ErrorCleanupCommand extends Command {

	public ErrorCleanupCommand() {
		super("errorcleanup", new String[]{"errcleanup", "errorcleaning"}, Category.SETTINGS, Permission.MANAGE_SERVER, 0, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
		var guildId = ctx.getGuild().getIdLong();
		var enabled = !guildSettingsCache.isErrorCleanupEnabled(guildId);
		var i18n = ctx.getI18n();
		guildSettingsCache.setErrorCleanupEnabled(guildId, enabled);
		ctx.reactLike();
		ctx.reply(i18n.get("commands.errorcleanup.other.done", enabled ? i18n.get("enabled") : i18n.get("disabled")));
	}
}