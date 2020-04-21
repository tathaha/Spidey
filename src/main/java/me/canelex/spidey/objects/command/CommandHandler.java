package me.canelex.spidey.objects.command;

import me.canelex.jda.api.entities.Message;
import me.canelex.spidey.Core;
import me.canelex.spidey.utils.Utils;

public class CommandHandler
{
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
		if (!commands.containsKey(command))
		{
			Utils.returnError("**" + command + "** isn't a valid command", msg);
			return;
		}
		final var cmd = commands.get(command);
		final var args = content.split("\\s+", cmd.getMaxArgs());
		cmd.action(args, msg);
	}
}