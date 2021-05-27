package dev.mlnr.spidey.commands.settings.general;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class VIPCommand extends Command {

	public VIPCommand() {
		super("vip", new String[]{}, Category.Settings.GENERAL, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		if (ctx.getUser().getIdLong() != 394607709741252621L) {
			ctx.replyErrorLocalized("command_failures.only_dev");
			return false;
		}
		var guildId = args.length == 0 ? ctx.getGuild().getIdLong() : Long.parseLong(args[0]);
		var generalSettings = ctx.getCache().getGuildSettingsCache().getGeneralSettings(guildId);
		var vip = !generalSettings.isVip();
		generalSettings.setVip(vip);
		ctx.reactLike();
		ctx.reply("VIP for guild **" + guildId + "** has been **" + (vip ? "enabled" : "disabled") + "**.");
		return true;
	}
}