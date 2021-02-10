package dev.mlnr.spidey.commands.settings.misc;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class ErrorCleanupCommand extends Command {

	public ErrorCleanupCommand() {
		super("errorcleanup", new String[]{"errcleanup", "errorcleaning"}, Category.Settings.MISC, Permission.MANAGE_SERVER, 0, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var miscSettings = ctx.getCache().getGuildSettingsCache().getMiscSettings(ctx.getGuild().getIdLong());
		var enabled = !miscSettings.isErrorCleanupEnabled();
		var i18n = ctx.getI18n();
		miscSettings.setErrorCleanupEnabled(enabled);
		ctx.reactLike();
		ctx.reply(i18n.get("commands.errorcleanup.other.done", enabled ? i18n.get("enabled") : i18n.get("disabled")));
	}
}