package dev.mlnr.spidey.commands.settings.misc;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class PrefixCommand extends Command {

	public PrefixCommand() {
		super("prefix", new String[]{}, Category.Settings.MISC, Permission.MANAGE_SERVER, 0, 4);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var miscSettings = ctx.getCache().getGuildSettingsCache().getMiscSettings(ctx.getGuild().getIdLong());
		var currentPrefix = miscSettings.getPrefix();
		var i18n = ctx.getI18n();
		if (args.length == 0) {
			if (currentPrefix.equals("s!")) {
				ctx.replyError(i18n.get("commands.prefix.other.default"));
			}
			else {
				miscSettings.setPrefix("s!");
				ctx.reply(i18n.get("commands.prefix.other.reset"));
			}
			return;
		}
		var newPrefix = args[0];
		if (currentPrefix.equals(newPrefix)) {
			ctx.replyError(i18n.get("commands.prefix.other.already_set", newPrefix));
			return;
		}
		if (newPrefix.length() > 10) {
			ctx.replyError(i18n.get("commands.prefix.other.longer"));
			return;
		}
		miscSettings.setPrefix(newPrefix);
		ctx.reply(i18n.get("commands.prefix.other.changed", newPrefix));
	}
}