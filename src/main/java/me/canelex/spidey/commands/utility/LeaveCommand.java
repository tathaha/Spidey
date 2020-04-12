package me.canelex.spidey.commands.utility;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Message;
import me.canelex.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import me.canelex.spidey.Core;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Emojis;
import me.canelex.spidey.utils.PermissionError;
import me.canelex.spidey.utils.Utils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class LeaveCommand implements ICommand
{
	@Override
	public final void action(final String[] args, final Message message)
	{
		final var guild = message.getGuild();
		final var channel = message.getChannel();

		final var requiredPermission = getRequiredPermission();
		if (!Utils.hasPerm(message.getMember(), requiredPermission))
			Utils.sendMessage(channel, PermissionError.getErrorMessage(requiredPermission));
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