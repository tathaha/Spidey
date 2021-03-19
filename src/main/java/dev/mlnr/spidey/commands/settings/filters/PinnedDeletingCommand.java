package dev.mlnr.spidey.commands.settings.filters;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class PinnedDeletingCommand extends Command {

	public PinnedDeletingCommand() {
		super("pinneddeleting", new String[]{"pinneddel", "pindel", "pinfilter", "pinnedfilter"}, Category.Settings.FILTERS, Permission.MANAGE_SERVER, 0, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var miscSettings = ctx.getCache().getGuildSettingsCache().getFiltersSettings(ctx.getGuild().getIdLong());
		var enabled = !miscSettings.isPinnedDeletingEnabled();
		var i18n = ctx.getI18n();
		miscSettings.setPinnedDeletingEnabled(enabled);
		ctx.reactLike();
		ctx.replyLocalized("commands.pinneddeleting.other.done", enabled ? i18n.get("enabled") : i18n.get("disabled"));
	}
}