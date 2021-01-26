package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class PrefixCommand extends Command {

	public PrefixCommand() {
		super("prefix", new String[]{}, Category.SETTINGS, Permission.MANAGE_SERVER, 0, 4);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
		var guild = ctx.getGuild();
		var guildId = guild.getIdLong();
		var currentPrefix = guildSettingsCache.getPrefix(guildId);
		var i18n = ctx.getI18n();
		if (args.length == 0) {
			if (currentPrefix.equals("s!")) {
				ctx.replyError(i18n.get("commands.prefix.other.default"));
			}
			else {
				guildSettingsCache.setPrefix(guildId, "s!");
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
		guildSettingsCache.setPrefix(guildId, newPrefix);
		ctx.reply(i18n.get("commands.prefix.other.changed", newPrefix));
	}
}