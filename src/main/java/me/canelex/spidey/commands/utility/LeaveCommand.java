package me.canelex.spidey.commands.utility;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Message;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.PermissionError;
import me.canelex.spidey.utils.Utils;

import java.time.Duration;

@SuppressWarnings("unused")
public class LeaveCommand implements ICommand
{
	@Override
	public final void action(final String[] args, final Message message)
	{
		final var guild = message.getGuild();
		final var channel = message.getChannel();

		final var requiredPermission = getRequiredPermission();
		if (Utils.hasPerm(message.getMember(), requiredPermission))
			Utils.sendMessage(channel, PermissionError.getErrorMessage(requiredPermission));
		else
		{
			channel.sendMessage("Bye.")
				   .delay(Duration.ofSeconds(5))
				   .flatMap(Message::delete)
				   .delay(Duration.ofSeconds(0))
				   .flatMap(ignored -> message.delete())
				   .flatMap(ignored -> guild.leave())
				   .queue();
		}
	}

	@Override
	public final String getDescription() { return "Spidey will leave your server"; }
	@Override
	public final Permission getRequiredPermission() { return Permission.ADMINISTRATOR; }
	@Override
	public final String getInvoke() { return "leave"; }
	@Override
	public final Category getCategory() { return Category.UTILITY; }
	@Override
	public final String getUsage() { return "s!leave"; }
}