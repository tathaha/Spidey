package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class VIPCommand extends Command {

	public VIPCommand() {
		super("vip", new String[]{}, Category.SETTINGS, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		if (ctx.getAuthor().getIdLong() != 394607709741252621L) {
			ctx.replyError(ctx.getI18n().get("command_failures.only_dev"));
			return;
		}
		var guildId = args.length == 0 ? ctx.getGuild().getIdLong() : Long.parseLong(args[0]);
		var generalSettings = ctx.getCache().getGuildSettingsCache().getGeneralSettings(guildId);
		var vip = !generalSettings.isVip();
		generalSettings.setVip(vip);
		ctx.reactLike();
		ctx.reply("VIP for guild **" + guildId + "** has been **" + (vip ? "enabled" : "disabled") + "**.");
	}
}