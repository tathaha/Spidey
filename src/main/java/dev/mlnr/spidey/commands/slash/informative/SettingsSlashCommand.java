package dev.mlnr.spidey.commands.slash.informative;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import static java.lang.String.format;

@SuppressWarnings("unused")
public class SettingsSlashCommand extends SlashCommand {
	public SettingsSlashCommand() {
		super("settings", "Shows the current settings for this server", Category.INFORMATIVE, Permission.UNKNOWN, 0);
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
		var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
		var guildId = ctx.getGuild().getIdLong();
		var miscSettings = guildSettingsCache.getMiscSettings(guildId);
		var embedBuilder = Utils.createEmbedBuilder(ctx.getUser());
		var i18n = ctx.getI18n();

		embedBuilder.setAuthor(i18n.get("commands.settings.title"));

		var setTemplate = " (" + i18n.get("commands.settings.set") + " /%s)";
		var none = i18n.get("commands.settings.none");

		var logChannel = miscSettings.getLogChannel();
		embedBuilder.addField(i18n.get("commands.settings.log"),
				logChannel == null ? none + format(setTemplate, "log") : logChannel.getAsMention(), false);

		var joinRole = miscSettings.getJoinRole();
		embedBuilder.addField(i18n.get("commands.settings.join"),
				joinRole == null ? none + format(setTemplate, "joinrole") : joinRole.getAsMention(), false);

		var djRole = guildSettingsCache.getMusicSettings(guildId).getDJRole();
		embedBuilder.addField(i18n.get("commands.settings.dj"),
				djRole == null ? none + format(setTemplate, "djrole") : djRole.getAsMention(), false);

		ctx.reply(embedBuilder);
		return true;
	}
}