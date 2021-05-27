package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.handlers.command.CooldownHandler;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.command.category.ICategory;
import dev.mlnr.spidey.utils.StringUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unused")
public class HelpCommand extends Command {

	public HelpCommand() {
		super("help", new String[]{"commands", "cmds"}, Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public boolean execute(String[] args, CommandContext ctx) {
		var commandsMap = CommandHandler.getCommands();
		var author = ctx.getAuthor();
		var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
		var guildId = ctx.getGuild().getIdLong();
		var prefix = guildSettingsCache.getMiscSettings(guildId).getPrefix();
		var i18n = ctx.getI18n();
		var eb = Utils.createEmbedBuilder(author)
				.setAuthor(i18n.get("commands.help.other.text"), "https://github.com/caneleex/Spidey", ctx.getJDA().getSelfUser().getEffectiveAvatarUrl());

		if (args.length == 0) {
			var commandsCopy = new HashMap<>(commandsMap);
			var entries = commandsCopy.entrySet();
			entries.removeIf(entry -> !ctx.getMember().hasPermission(entry.getValue().getRequiredPermission()));
			var hidden = commandsMap.size() - commandsCopy.size();
			var iter = entries.iterator();
			var valueSet = new HashSet<>();
			while (iter.hasNext()) { // remove all duplicate commands to just show invokes, not aliases
				if (!valueSet.add(iter.next().getValue())) {
					iter.remove();
				}
			}
			var categories = new HashMap<ICategory, List<Command>>();
			var nsfwHidden = false;
			commandsCopy.values().forEach(cmd -> categories.computeIfAbsent(cmd.getCategory(), k -> new ArrayList<>()).add(cmd));
			if (!ctx.getTextChannel().isNSFW()) {
				categories.remove(Category.NSFW);
				nsfwHidden = true;
			}

			var commandsStringBuilder = new StringBuilder();
			var settingsBuilder = new StringBuilder();
			settingsBuilder.append("\n\u2699\uFE0F Settings");

			for (var entry : categories.entrySet()) {
				var category = entry.getKey();
				var categoryName = category.getFriendlyName();
				var commandz = listToString(entry.getValue());

				if (category instanceof Category.Settings) {
					settingsBuilder.append("\n<:empty:806627051905089576>˪ ");
					settingsBuilder.append(categoryName);
					settingsBuilder.append(" ").append("-").append(" ");
					settingsBuilder.append(commandz);
					continue;
				}
				commandsStringBuilder.append("\n");
				commandsStringBuilder.append(categoryName);
				commandsStringBuilder.append(" ").append("-").append(" ");
				commandsStringBuilder.append(commandz);
			}

			commandsStringBuilder.append(settingsBuilder);

			eb.setDescription(i18n.get("commands.help.other.embed_content", prefix, commandsStringBuilder.toString(), prefix));
			if (hidden > 0) {
				eb.appendDescription(i18n.get("commands.help.other.hidden.text", hidden));
			}
			if (nsfwHidden) {
				eb.appendDescription(i18n.get("commands.help.other.hidden.nsfw"));
			}
			ctx.reply(eb);
			return true;
		}
		var invoke = args[0].toLowerCase();
		var command = commandsMap.get(invoke);
		if (command == null) {
			var similar = StringUtils.getSimilarCommand(invoke);
			ctx.replyError(i18n.get("command_failures.invalid.message", invoke) + " " + (similar == null
					? i18n.get("command_failures.invalid.check_help", prefix)
					: i18n.get("command_failures.invalid.suggestion", similar)));
			return false;
		}
		invoke = command.getInvoke();
		var none = i18n.get("commands.help.other.command_info.info_none");
		var requiredPermission = command.getRequiredPermission();
		var aliases = command.getAliases();
		var generalSettings = guildSettingsCache.getGeneralSettings(guildId);
		var cooldown = CooldownHandler.adjustCooldown(command.getCooldown(), generalSettings.isVip());

		eb.setAuthor(i18n.get("commands.help.other.viewing") + " - " + invoke);
		eb.addField(i18n.get("commands.help.other.command_info.description"),
				i18n.get("commands." + invoke + ".description"), false);

		eb.addField(i18n.get("commands.help.other.command_info.usage"),
				"`" + prefix + i18n.get("commands." + invoke + ".usage") + "` " +
						i18n.get("commands.help.other.command_info.usage_required_optional"), false);

		eb.addField(i18n.get("commands.help.other.command_info.category"), command.getCategory().getFriendlyName(), false);
		eb.addField(i18n.get("commands.help.other.command_info.required_permission"), requiredPermission == Permission.UNKNOWN
				? none : requiredPermission.getName(), false);
		eb.addField(i18n.get("commands.help.other.command_info.aliases"), aliases.length == 0 ? none : String.join(", ", aliases), false);
		eb.addField(i18n.get("commands.help.other.command_info.cooldown"), cooldown == 0 ? none : cooldown + " " +
				i18n.get("commands.help.other.command_info.seconds"), false);

		if (!generalSettings.isVip()) {
			eb.addBlankField(false);
			eb.addField(i18n.get("commands.help.other.donate.title"), i18n.get("commands.help.other.donate.text"), false);
		}
		ctx.reply(eb);
		return true;
	}

	private String listToString(List<Command> commands) {
		var builder = new StringBuilder();
		for (var i = 0; i < commands.size(); i++) {
			var cmd = commands.get(i);
			builder.append("`").append(cmd.getInvoke()).append("`");
			if (i != commands.size() - 1) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}
}