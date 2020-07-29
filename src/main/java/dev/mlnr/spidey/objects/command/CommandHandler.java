package dev.mlnr.spidey.objects.command;

import dev.mlnr.spidey.utils.KSoftAPIHelper;
import dev.mlnr.spidey.utils.Utils;
import io.github.classgraph.ClassGraph;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static dev.mlnr.spidey.objects.command.Cooldowns.cooldown;
import static dev.mlnr.spidey.objects.command.Cooldowns.isOnCooldown;

public class CommandHandler
{
	private static final Map<String, Command> COMMANDS = new HashMap<>();
	private static final Logger LOG = LoggerFactory.getLogger(CommandHandler.class);
	private static final ClassGraph CLASS_GRAPH = new ClassGraph().whitelistPackages("dev.mlnr.spidey.commands");
	private static final String NO_PERMS = "Action can't be completed because you don't have **%s** permission";
	public static final MessageEmbed ADMIN_WARNING = new EmbedBuilder().setAuthor("Potential security risk").setColor(Color.RED)
												     .appendDescription("I have Administrator permission for this Discord server.")
												     .appendDescription("\nAs this is a huge security risk, __i'll refuse to handle any command__.")
												     .appendDescription("\n\nBots shouldn't have Administrator permission unless you *need* it for **your** bot.")
												     .appendDescription("\nPlease __remove this permission and i'll work properly again__.").build();

	private CommandHandler()
	{
		super();
	}

	public static void handle(final Message msg, final String prefix)
	{
		final var content = msg.getContentRaw().substring(prefix.length());
		if (content.isEmpty())
		{
			Utils.returnError("Please specify a command", msg);
			return;
		}
		final var command = content.contains(" ") ? content.substring(0, content.indexOf(' ')) : content;
		final var cmd = COMMANDS.get(command.toLowerCase());
		if (cmd == null)
		{
			Utils.returnError("**" + command + "** isn't a valid command", msg);
			return;
		}
		final var requiredPermission = cmd.getRequiredPermission();
		final var member = msg.getMember();
		if (!member.hasPermission(requiredPermission))
		{
			Utils.returnError(String.format(NO_PERMS, requiredPermission.getName()), msg);
			return;
		}
		final var guildId = msg.getGuild().getIdLong();
		if (isOnCooldown(guildId, cmd))
		{
			Utils.returnError("The command is on cooldown", msg);
			return;
		}

		// API DEPENDANT COMMANDS HANDLING
		final var channel = msg.getTextChannel();
		final var category = cmd.getCategory();
		final var nsfw = category == Category.NSFW;
		if ((category == Category.FUN && cmd.getCooldown() > 0) || nsfw) // if a command has a cooldown, i can assume it requires an api
		{
			if (nsfw && !channel.isNSFW())
			{
				Utils.returnError("You can use nsfw commands only in nsfw channels", msg);
				return;
			}
			Utils.sendMessage(channel, KSoftAPIHelper.getImage(cmd.getInvoke(), member, nsfw));
			cooldown(guildId, cmd);
			return;
		}
		//

		final var maxArgs = cmd.getMaxArgs();
		final var tmp = content.split("\\s+", maxArgs > 0 ? maxArgs + 1 : maxArgs);
		final var args = Arrays.copyOfRange(tmp, 1, tmp.length);
		cmd.execute(args, msg);
		cooldown(guildId, cmd);
	}

	public static void registerCommands()
	{
		try (final var result = CLASS_GRAPH.scan())
		{
			for (final var cls : result.getAllClasses())
			{
				final var cmd = (Command) cls.loadClass().getDeclaredConstructor().newInstance();
				COMMANDS.put(cmd.getInvoke(), cmd);
				for (final var alias : cmd.getAliases())
					COMMANDS.put(alias, cmd);
			}
		}
		catch (final Exception e)
		{
			LOG.error("There was an error while registering the commands!", e);
		}
	}
	
	public static Map<String, Command> getCommands()
	{
		return COMMANDS;
	}
}