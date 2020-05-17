package me.canelex.spidey.commands.utility;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Message;
import me.canelex.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import me.canelex.spidey.Core;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Emojis;
import me.canelex.spidey.utils.Utils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class LeaveCommand extends Command
{
	public LeaveCommand()
	{
		super("leave", new String[]{}, "Spidey will leave your server", "leave", Category.UTILITY, Permission.ADMINISTRATOR, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var guild = message.getGuild();
		final var channel = message.getChannel();

		final var requiredPermission = getRequiredPermission();
		if (!Utils.hasPerm(message.getMember(), requiredPermission))
			Utils.getPermissionsError(requiredPermission, message);
		else
		{
			Utils.deleteMessage(message);
			channel.sendMessage("Do you really want me to leave?").queue(msg ->
			{
				msg.addReaction(Emojis.CHECK).queue();
				msg.addReaction(Emojis.CROSS).queue();
				Core.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class,
						ev ->
						{
							final var name = ev.getReactionEmote().getName();
							return ev.getUser() == message.getAuthor() && ev.getMessageIdLong() == msg.getIdLong() && (name.equals(Emojis.CHECK) || name.equals(Emojis.CROSS));
						},
						ev ->
						{
							if (ev.getReactionEmote().getName().equals(Emojis.CHECK))
							{
								Utils.deleteMessage(msg);
								channel.sendMessage("I'm sad that i have to leave.. **Thanks for using me tho**!")
									  .delay(Duration.ofSeconds(5))
									  .flatMap(Message::delete)
									  .flatMap(ignored -> guild.leave())
									  .queue();
							}
							else
							{
								Utils.deleteMessage(msg);
								channel.sendMessage("I'm not leaving this time, yay. :tada:")
									   .delay(Duration.ofSeconds(5))
									   .flatMap(Message::delete)
									   .queue();
							}

						}, 1, TimeUnit.MINUTES, () -> Utils.returnError("Sorry, but you took too long. I'm not leaving this time", msg));
			});
		}
	}
}