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
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class HelpCommand extends Command {
	public HelpCommand() {
		super("help", "Shows the help message", Category.INFORMATIVE, Permission.UNKNOWN, 0,
				new OptionData(OptionType.STRING, "command", "The command to get help for"));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var commandsMap = CommandHandler.getCommands();
		var author = ctx.getUser();
		var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
		var guildId = ctx.getGuild().getIdLong();
		var i18n = ctx.getI18n();
		var embedBuilder = Utils.createEmbedBuilder(author)
				.setAuthor(i18n.get("commands.help.text"), "https://github.com/caneleeex/Spidey", ctx.getJDA().getSelfUser().getEffectiveAvatarUrl());
		var commandOption = ctx.getStringOption("command");

		if (commandOption == null) {
			var commandsCopy = new HashMap<>(commandsMap);
			var entries = commandsCopy.entrySet();
			entries.removeIf(entry -> !ctx.getMember().hasPermission(entry.getValue().getRequiredPermission()));
			var hidden = commandsMap.size() - commandsCopy.size();
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

			embedBuilder.setDescription(i18n.get("commands.help.embed_content", commandsStringBuilder.toString()));
			if (hidden > 0) {
				embedBuilder.appendDescription(i18n.get("commands.help.hidden.text", hidden));
			}
			if (nsfwHidden) {
				embedBuilder.appendDescription(i18n.get("commands.help.hidden.nsfw"));
			}
			ctx.reply(embedBuilder);
			return true;
		}
		var command = commandsMap.get(commandOption);
		if (command == null) {
			var similar = StringUtils.getSimilarCommand(commandOption);
			ctx.replyError(i18n.get("command_failures.invalid.message", commandOption) + " " + (similar == null
					? i18n.get("command_failures.invalid.check_help")
					: i18n.get("command_failures.invalid.suggestion", similar)));
			return false;
		}
		commandOption = command.getName();
		var none = i18n.get("commands.help.command_info.info_none");
		var requiredPermission = command.getRequiredPermission();
		var generalSettings = guildSettingsCache.getGeneralSettings(guildId);
		var cooldown = CooldownHandler.adjustCooldown(command.getCooldown(), generalSettings.isVip());

		embedBuilder.setAuthor(i18n.get("commands.help.viewing") + " - " + commandOption);
		embedBuilder.addField(i18n.get("commands.help.command_info.category"), command.getCategory().getFriendlyName(), false);
		embedBuilder.addField(i18n.get("commands.help.command_info.required_permission"), requiredPermission == Permission.UNKNOWN
				? none : requiredPermission.getName(), false);
		embedBuilder.addField(i18n.get("commands.help.command_info.cooldown"), cooldown == 0 ? none : cooldown + " " +
				i18n.get("commands.help.command_info.seconds"), false);

		if (!generalSettings.isVip()) {
			embedBuilder.addBlankField(false);
			embedBuilder.addField(i18n.get("commands.help.donate.title"), i18n.get("commands.help.donate.text"), false);
		}
		ctx.reply(embedBuilder);
		return true;
	}

	private String listToString(List<Command> commands) {
		var builder = new StringBuilder();
		for (var i = 0; i < commands.size(); i++) {
			var cmd = commands.get(i);
			builder.append("`").append(cmd.getName()).append("`");
			if (i != commands.size() - 1) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}
}