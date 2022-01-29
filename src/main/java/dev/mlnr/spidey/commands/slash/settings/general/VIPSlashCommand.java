package dev.mlnr.spidey.commands.slash.settings.general;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class VIPSlashCommand extends SlashCommand {
	public VIPSlashCommand() {
		super("vip", "Enables/disables VIP for a guild", Category.Settings.GENERAL, Permission.UNKNOWN, 0,
				new OptionData(OptionType.INTEGER, "guild_id", "The ID of the guild to enable/disable VIP for"));
		withFlags(SlashCommand.Flags.DEV_ONLY);
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
		var guildIdOption = ctx.getLongOption("guild_id");
		var guildId = guildIdOption == null ? ctx.getGuild().getIdLong() : guildIdOption;
		var generalSettings = ctx.getCache().getGuildSettingsCache().getGeneralSettings(guildId);
		var vip = !generalSettings.isVip();
		generalSettings.setVip(vip);
		ctx.reply("VIP for guild **" + guildId + "** has been **" + (vip ? "enabled" : "disabled") + "**.");
		return true;
	}
}