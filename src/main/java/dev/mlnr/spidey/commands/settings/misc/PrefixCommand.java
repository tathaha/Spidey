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
	public boolean execute(String[] args, CommandContext ctx) {
		var miscSettings = ctx.getCache().getGuildSettingsCache().getMiscSettings(ctx.getGuild().getIdLong());
		var currentPrefix = miscSettings.getPrefix();
		if (args.length == 0) {
			if (currentPrefix.equals("s!")) {
				ctx.replyErrorLocalized("commands.prefix.other.default");
			}
			else {
				miscSettings.setPrefix("s!");
				ctx.replyLocalized("commands.prefix.other.reset");
			}
			return true;
		}
		var newPrefix = args[0];
		if (currentPrefix.equals(newPrefix)) {
			ctx.replyErrorLocalized("commands.prefix.other.already_set", newPrefix);
			return false;
		}
		if (newPrefix.length() > 10) {
			ctx.replyErrorLocalized("commands.prefix.other.longer");
			return false;
		}
		miscSettings.setPrefix(newPrefix);
		ctx.replyLocalized("commands.prefix.other.changed", newPrefix);
		return true;
	}
}