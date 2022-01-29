package dev.mlnr.spidey.handlers.command;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.objects.commands.context.ContextCommand;
import dev.mlnr.spidey.objects.commands.context.ContextCommandContext;
import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.utils.CommandUtils;
import dev.mlnr.spidey.utils.Utils;
import io.github.classgraph.ClassGraph;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static dev.mlnr.spidey.handlers.command.CooldownHandler.cooldown;
import static dev.mlnr.spidey.handlers.command.CooldownHandler.isOnCooldown;
import static dev.mlnr.spidey.utils.Utils.replyErrorWithoutContext;

@SuppressWarnings("StaticCollection")
public class CommandHandler {
	private static final Map<String, SlashCommand> SLASH_COMMANDS = new HashMap<>();
	private static final Map<String, ContextCommand<?>> CONTEXT_COMMANDS = new HashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

	private CommandHandler() {}

	public static void handleSlashCommand(SlashCommandInteractionEvent event, Cache cache) {
		var commandName = event.getName();
		var command = SLASH_COMMANDS.get(commandName);
		var member = event.getMember();
		var guildId = event.getGuild().getIdLong();
		var i18n = cache.getGuildSettingsCache().getMiscSettings(guildId).getI18n();

		if (!CommandUtils.hasPermission(command, member)) {
			replyErrorWithoutContext(event, i18n.get("command_failures.no_perms", command.getRequiredPermission().getName()));
			return;
		}
		if (!CommandUtils.checkForDevCommand(command, member.getUser())) {
			replyErrorWithoutContext(event, i18n.get("command_failures.only_dev"));
			return;
		}
		var channel = event.getChannel();
		if (channel.getType().isThread() && !command.supportsThreads()) {
			replyErrorWithoutContext(event, i18n.get("command_failures.no_threads"));
			return;
		}
		var userId = member.getIdLong();
		if (isOnCooldown(userId, command)) {
			replyErrorWithoutContext(event, i18n.get("command_failures.cooldown"));
			return;
		}
		var vip = cache.getGuildSettingsCache().getGeneralSettings(guildId).isVip();
		var executed = command.execute(new SlashCommandContext(event, command.shouldHideResponse(), i18n, cache));
		if (executed) {
			cooldown(userId, command, vip);
		}
	}

	public static void handleContextCommand(GenericContextInteractionEvent<?> event, Cache cache) {
		var commandName = event.getName();
		var command = CONTEXT_COMMANDS.get(commandName);
		var guildId = event.getGuild().getIdLong();
		var i18n = cache.getGuildSettingsCache().getMiscSettings(guildId).getI18n();
		command.execute(new ContextCommandContext(event, i18n, cache));
	}

	public static void loadCommands(JDA jda) {
		var commandsUpdate = jda.getSelfUser().getIdLong() == Utils.SPIDEY_ID
				? jda.updateCommands()
				: jda.getGuildById(772435739664973825L).updateCommands();
		try (var result = new ClassGraph().acceptPackages("dev.mlnr.spidey.commands").scan()) {
			var hideOption = new OptionData(OptionType.BOOLEAN, "hide", "Whether to hide the response");

			for (var cls : result.getAllClasses()) {
				var clazz = cls.loadClass();
				var command = (CommandDataImpl) clazz.getDeclaredConstructor().newInstance();
				var commandName = command.getName();
				if (clazz.getSuperclass() == SlashCommand.class) {
					SLASH_COMMANDS.put(commandName, (SlashCommand) command);
					command.addOptions(hideOption);
				}
				else {
					CONTEXT_COMMANDS.put(commandName, (ContextCommand<?>) command);
				}
				commandsUpdate.addCommands(command);
			}
			commandsUpdate.queue();
		}
		catch (Exception e) {
			logger.error("There was an error while registering the commands, exiting", e);
			System.exit(1);
		}
	}

	public static Map<String, SlashCommand> getSlashCommands() {
		return SLASH_COMMANDS;
	}
}