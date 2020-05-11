package me.canelex.spidey.commands.moderation;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Message;
import me.canelex.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import me.canelex.spidey.Core;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Emojis;
import me.canelex.spidey.utils.Utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class DeleteCommand implements ICommand
{
	private int count;

	@Override
	public final void action(final String[] args, final Message message)
	{
		final var channel = message.getChannel();
		final var mentionedUsers = message.getMentionedUsers();

		message.delete().queue();

		final var requiredPermission = getRequiredPermission();
		if (!Utils.hasPerm(message.getMember(), requiredPermission))
		{
			Utils.getPermissionsError(requiredPermission, message);
			return;
		}

		if (args.length < 2)
		{
			Utils.returnError("Wrong syntax", message);
			return;
		}
		var amount = 0;
		if (mentionedUsers.isEmpty())
		{
			try
			{
				amount = Integer.parseUnsignedInt(args[1]);
			}
			catch (final NumberFormatException ignored)
			{
				Utils.returnError("Entered value is either negative or not a number", message);
				return;
			}
			if (amount == 0)
			{
				Utils.returnError("Please enter a number from 1-100", message);
				return;
			}

			channel.getIterableHistory().cache(false).takeAsync(amount).thenAcceptAsync(messages ->
			{
				final var toDelete = new ArrayList<>(messages);
				final var pinned = messages.stream().filter(Message::isPinned).count();
				if (pinned > 0)
				{
					final var equalsOne = pinned == 1;
					channel.sendMessage("There " + (equalsOne ? "is" : "are") + " **" + pinned + "** pinned message" + (equalsOne ? "" : "s") + " that you're going to delete. Are you sure?").queue(msg ->
					{
						final var wastebasket = "\uD83D\uDDD1";
						msg.addReaction(Emojis.CHECK).queue();
						msg.addReaction(Emojis.CROSS).queue();
						msg.addReaction(wastebasket).queue();
						Core.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class,
								ev ->
								{
									final var name = ev.getReactionEmote().getName();
									return ev.getUser() == message.getAuthor() && ev.getMessageIdLong() == msg.getIdLong() && (name.equals(Emojis.CHECK) || name.equals(Emojis.CROSS) || name.equals(wastebasket));
								},
								ev ->
								{
									switch (ev.getReactionEmote().getName())
									{
										case Emojis.CHECK:
											Utils.deleteMessage(msg);
											break;
										case Emojis.CROSS:
											Utils.deleteMessage(msg);
											return;
										case wastebasket:
											toDelete.removeIf(Message::isPinned);
											final var size = messages.size();
											channel.getHistoryAfter(messages.get(size - 1), size - Math.toIntExact(pinned)).queue(msgs -> toDelete.addAll(msgs.getRetrievedHistory()));
											Utils.deleteMessage(msg);
											break;
									}
								}, 1, TimeUnit.MINUTES, () -> Utils.returnError("Sorry, but you took too long", msg));
					});
				}
				count = toDelete.size();
				CompletableFuture<Void> future;
				if (count == 1)
					future = toDelete.get(0).delete().submit();
				else
					future = CompletableFuture.allOf(channel.purgeMessages(toDelete).toArray(new CompletableFuture[0]));
				future.thenRunAsync(() -> channel.sendMessage(Utils.generateSuccess(count, null))
						                         .delay(Duration.ofSeconds(5))
					   							 .flatMap(Message::delete)
					   							 .queue());
			});
		}
		else
		{
			final var user = mentionedUsers.get(0);
			try
			{
				amount = Integer.parseUnsignedInt(args[2]);
			}
			catch (final NumberFormatException ignored)
			{
				Utils.returnError("Entered value is either negative or not a number", message);
				return;
			}
			if (amount == 0)
			{
				Utils.returnError("Please enter a number from 1-100", message);
				return;
			}
			if (amount == 100)
				amount = 99;

			final var a = amount;
			channel.getIterableHistory().cache(false).takeAsync(100).thenAccept(messages ->
			{
				final var newList = messages.stream().filter(m -> m.getAuthor().equals(user)).limit(a).collect(Collectors.toList());
				CompletableFuture<Void> future;
				if (newList.size() == 1)
					future = newList.get(0).delete().submit();
				else
					future = CompletableFuture.allOf(channel.purgeMessages(newList).toArray(new CompletableFuture[0]));
				future.thenRunAsync(() -> channel.sendMessage(Utils.generateSuccess(newList.size(), user))
						                         .delay(Duration.ofSeconds(5))
												 .flatMap(Message::delete)
												 .queue());
			});
		}
	}

	@Override
	public final String getDescription() { return "Deletes messages (by mentioned user)"; }
	@Override
	public final Permission getRequiredPermission() { return Permission.MESSAGE_MANAGE; }
	@Override
    public final int getMaxArgs() { return 3; }
	@Override
	public final String getInvoke() { return "d"; }
	@Override
	public final Category getCategory() { return Category.MODERATION; }
	@Override
	public final String getUsage() { return "s!d <count/@someone> <count>"; }
}