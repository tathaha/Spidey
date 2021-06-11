package dev.mlnr.spidey.commands.settings.general;

import dev.mlnr.spidey.objects.command.CommandBase;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class VIPCommand extends CommandBase {
	public VIPCommand() {
		super("vip", "Enables/disables VIP for a guild", Category.Settings.GENERAL, Permission.UNKNOWN, 0,
				new OptionData(OptionType.INTEGER, "guild_id", "The ID of the guild to enable/disable VIP for"));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		if (ctx.getUser().getIdLong() != 394607709741252621L) {
			ctx.replyErrorLocalized("command_failures.only_dev");
			return false;
		}
		var guildIdOption = ctx.getLongOption("guild_id");
		var guildId = guildIdOption == null ? ctx.getGuild().getIdLong() : guildIdOption;
		var generalSettings = ctx.getCache().getGuildSettingsCache().getGeneralSettings(guildId);
		var vip = !generalSettings.isVip();
		generalSettings.setVip(vip);
		ctx.reply("VIP for guild **" + guildId + "** has been **" + (vip ? "enabled" : "disabled") + "**.");
		return true;
	}
}