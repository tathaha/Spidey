package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import static java.lang.String.format;

@SuppressWarnings("unused")
public class SettingsCommand extends Command {

	public SettingsCommand() {
		super("settings", Category.INFORMATIVE, Permission.UNKNOWN, 0);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
		var guildId = ctx.getGuild().getIdLong();
		var miscSettings = guildSettingsCache.getMiscSettings(guildId);
		var eb = Utils.createEmbedBuilder(ctx.getUser());
		var i18n = ctx.getI18n();

		eb.setAuthor(i18n.get("commands.settings.other.title"));

		var setTemplate = " (" + i18n.get("commands.settings.other.set") + " /%s)";
		var none = i18n.get("commands.settings.other.none");

		var logChannel = miscSettings.getLogChannel();
		eb.addField(i18n.get("commands.settings.other.log"),
				logChannel == null ? none + format(setTemplate, "log") : logChannel.getAsMention(), false);

		var joinRole = miscSettings.getJoinRole();
		eb.addField(i18n.get("commands.settings.other.join"),
				joinRole == null ? none + format(setTemplate, "joinrole") : joinRole.getAsMention(), false);

		var djRole = guildSettingsCache.getMusicSettings(guildId).getDJRole();
		eb.addField(i18n.get("commands.settings.other.dj"),
				djRole == null ? none + format(setTemplate, "djrole") : djRole.getAsMention(), false);

		ctx.reply(eb);
		return true;
	}
}