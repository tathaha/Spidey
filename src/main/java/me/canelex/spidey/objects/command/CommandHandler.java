package me.canelex.spidey.objects.command;

import me.canelex.spidey.Core;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class CommandHandler
{
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
		if (content.length() == 0)
		{
			Utils.returnError("Please specify a command", msg);
			return;
		}
		final var command = content.contains(" ") ? content.substring(0, content.indexOf(' ')) : content;
		final var commands = Core.getCommands();
		final var cmd = commands.get(command);
		if (cmd == null)
		{
			Utils.returnError("**" + command + "** isn't a valid command", msg);
			return;
		}
		final var args = content.split("\\s+", cmd.getMaxArgs());
		cmd.execute(args, msg);
	}
}