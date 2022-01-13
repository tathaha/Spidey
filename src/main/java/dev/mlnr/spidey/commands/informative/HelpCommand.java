package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.handlers.command.CooldownHandler;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.command.category.ICategory;
import dev.mlnr.spidey.objects.command.category.ICategory.CategoryFlag;
import dev.mlnr.spidey.utils.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class HelpCommand extends Command {
	public HelpCommand() {
		super("help", "Shows the help message", Category.INFORMATIVE, Permission.UNKNOWN, 0,
				new OptionData(OptionType.STRING, "command", "The command to get help for").setAutoComplete(true));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var chosenCommand = ctx.getStringOption("command");
		var commands = CommandHandler.getCommands();
		var embedBuilder = Utils.createEmbedBuilder(ctx.getUser());
		var i18n = ctx.getI18n();

		if (chosenCommand == null) {
			var member = ctx.getMember();
			var groupedCommands = commands // TODO figure out sorting the categories so the category order is constant
					.values()
					.stream()
					.filter(command -> CommandUtils.canRunCommand(command, member))
					.collect(Collectors.groupingBy(command -> command.getCategory().getFlag(), Collectors.groupingBy(Command::getCategory)));

			embedBuilder.setAuthor(i18n.get("commands.help.text"), "https://spidey.mlnr.dev", ctx.getJDA().getSelfUser().getEffectiveAvatarUrl());
			groupedCommands.get(CategoryFlag.BASE).forEach((category, commandz) -> {
				embedBuilder.appendDescription("\n");
				appendCommands(embedBuilder, category, commandz);
			});

			embedBuilder.appendDescription("\n\u2699\uFE0F Settings");
			groupedCommands.get(CategoryFlag.SETTINGS).forEach((category, commandz) -> {
				embedBuilder.appendDescription("\n<:empty:806627051905089576>˪ ");
				appendCommands(embedBuilder, category, commandz);
			});
			embedBuilder.appendDescription(i18n.get("commands.help.more_info"));
		}
		else {
			var command = commands.get(chosenCommand);
			if (command == null) {
				var similar = StringUtils.getSimilarCommand(chosenCommand);
				ctx.replyError(i18n.get("commands.help.command_info.invalid.message", chosenCommand) + " " + (similar == null
						? i18n.get("commands.help.command_info.invalid.check_help")
						: i18n.get("commands.help.command_info.invalid.suggestion", similar)));
				return false;
			}
			var requiredPermission = command.getRequiredPermission();
			var none = i18n.get("commands.help.command_info.info_none");
			var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
			var guildId = ctx.getGuild().getIdLong();
			var isVip = guildSettingsCache.getGeneralSettings(guildId).isVip();
			var cooldown = CooldownHandler.adjustCooldown(command.getCooldown(), isVip);

			embedBuilder.setAuthor(i18n.get("commands.help.viewing", chosenCommand));
			embedBuilder.addField(i18n.get("commands.help.command_info.category"), command.getCategory().getFriendlyName(), false);
			embedBuilder.addField(i18n.get("commands.help.command_info.required_permission"), requiredPermission == Permission.UNKNOWN
					? none : requiredPermission.getName(), false);
			embedBuilder.addField(i18n.get("commands.help.command_info.cooldown"), cooldown == 0 ? none : cooldown + " " +
					i18n.get("commands.help.command_info.seconds"), false);

			if (!isVip) {
				embedBuilder.addBlankField(false);
				embedBuilder.addField(i18n.get("commands.help.donate.title"), i18n.get("commands.help.donate.text"), false);
			}
		}
		ctx.reply(embedBuilder);
		return true;
	}

	private void appendCommands(EmbedBuilder embedBuilder, ICategory category, List<Command> commands) {
		embedBuilder.appendDescription(category.getFriendlyName());
		embedBuilder.appendDescription(" ").appendDescription("-").appendDescription(" ");
		embedBuilder.appendDescription(listToString(commands));
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