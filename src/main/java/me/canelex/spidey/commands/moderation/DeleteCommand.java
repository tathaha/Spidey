package me.canelex.spidey.commands.moderation;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Message;
import me.canelex.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import me.canelex.spidey.Core;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Emojis;
import me.canelex.spidey.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unused", "StringBufferReplaceableByString"})
public class DeleteCommand implements ICommand
{
    @Override
    public final void action(final String[] args, final Message message)
    {
        final var channel = message.getChannel();
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
        try
        {
            amount = Integer.parseInt(args[1]);
        }
        catch (final NumberFormatException ignored)
        {
            Utils.returnError("Entered value is either negative or not a number", message);
            return;
        }
        if (amount > 100 || amount == 0)
        {
            Utils.returnError("Please enter a number from 1-100", message);
            return;
        }

        final var mentioned = message.getMentionedUsers();
        final var it = channel.getIterableHistory();
        final var finalAmount = amount;
        final var action = mentioned.isEmpty() ? it.takeAsync(amount)
                                               : it.takeWhileAsync(amount, msg -> msg.getAuthor().equals(mentioned.get(0)));
        action.thenAcceptAsync(messages ->
        {
            final var size = messages.size();
            final var toDelete = new ArrayList<>(messages);
            final var pinned = messages.stream().filter(Message::isPinned).count();

            if (pinned > 0)
            {
                final var equalsOne = pinned == 1;
                final var builder = new StringBuilder("There ");
                builder.append(equalsOne ? "is" : "are").append(" **").append(pinned)
                       .append(" ** pinned message").append(equalsOne ? "" : "s").append(" that you're gonna delete. Proceed?")
                       .append("\n\nNote: You can also remove pinned messages from the deletion process by reacting with :wastebasket:.");
                channel.sendMessage(builder.toString()).queue(msg ->
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
                                case Emojis.CROSS:
                                    Utils.deleteMessage(msg);
                                    return;
                                case wastebasket:
                                    toDelete.removeIf(Message::isPinned);
                                    channel.getHistoryAfter(toDelete.get(toDelete.size() - 1), finalAmount - toDelete.size()).queue(after -> toDelete.addAll(after.getRetrievedHistory()));
                                    break;
                                default: break;
                            }
                        }, 1, TimeUnit.MINUTES, () -> Utils.returnError("Sorry, you took too long", message));
                });
            }
        });
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