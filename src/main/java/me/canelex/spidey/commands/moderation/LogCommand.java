package me.canelex.spidey.commands.moderation;

import me.canelex.spidey.MySQL;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.time.Duration;

@SuppressWarnings("unused")
public class LogCommand extends Command
{
	public LogCommand()
	{
		super ("log", new String[]{}, "Sets log channel", "log", Category.MODERATION, Permission.ADMINISTRATOR, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var guild = message.getGuild();
		final var idLong = guild.getIdLong();
		final var channel = message.getChannel();

		final var requiredPermission = getRequiredPermission();
		if (Utils.hasPerm(message.getMember(), requiredPermission))
		{
			Utils.deleteMessage(message);
			if (guild.getSystemChannel() != null)
				guild.getManager().setSystemChannel(null).queue();

			final var channelId = channel.getIdLong();
			if (MySQL.getChannel(idLong) == channelId)
			{
				final var defaultChannel = guild.getDefaultChannel();
				if (defaultChannel == null)
					Utils.returnError("There is no default channel to set as the log channel", message);
				else
				{
					MySQL.setChannel(idLong, defaultChannel.getIdLong());
					channel.sendMessage(":white_check_mark: The log channel has been set to " + defaultChannel.getAsMention() + ". Type this command again in the channel you want to set as the log channel.")
						   .delay(Duration.ofSeconds(5))
						   .flatMap(Message::delete)
						   .queue();
				}
			}
			else
			{
				MySQL.setChannel(idLong, channelId);
				channel.sendMessage(":white_check_mark: The log channel has been set to <#" + channelId + ">. Type this command again to set the log channel to the default guild channel (if present).")
					   .delay(Duration.ofSeconds(5))
					   .flatMap(Message::delete)
					   .queue();
			}
		}
		else
			Utils.getPermissionsError(requiredPermission, message);
	}
}