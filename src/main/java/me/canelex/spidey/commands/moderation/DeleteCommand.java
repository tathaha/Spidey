package me.canelex.spidey.commands.moderation;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Message;
import me.canelex.jda.api.entities.MessageChannel;
import me.canelex.jda.api.entities.User;
import me.canelex.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import me.canelex.spidey.Core;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Emojis;
import me.canelex.spidey.utils.Utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "StringBufferReplaceableByString"})
public class DeleteCommand implements ICommand
{
    @Override
    public final void action(final String[] args, final Message message)
    {
        final var channel = message.getChannel();
        final var requiredPermission = getRequiredPermission();

        message.delete().complete();

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
        final var user = mentioned.isEmpty() ? null : mentioned.get(0);
        final var finalAmount = amount;
        channel.getIterableHistory().takeAsync(100).thenAcceptAsync(messages ->
        {
            final var msgs = user == null ? messages.subList(0, finalAmount) : messages.stream().filter(msg -> msg.getAuthor().equals(user)).limit(finalAmount).collect(Collectors.toList());
            if (msgs.isEmpty())
            {
                Utils.returnError("There are no messages to be deleted.", message);
                return;
            }
            final var toDelete = new ArrayList<>(msgs);
            final var pinned = msgs.stream().filter(Message::isPinned).count();

            if (pinned > 0)
            {
                final var equalsOne = pinned == 1;
                final var builder = new StringBuilder("There ");
                builder.append(equalsOne ? "is" : "are").append(" **").append(pinned)
                       .append("** pinned message").append(equalsOne ? "" : "s").append(" that will be deleted if you proceed.")
                       .append("\n\nReacting with :white_check_mark: will delete a given amount of messages including pinned ones.")
                       .append("\nReacting with :x: will cancel the deletion process.")
                       .append("\nReacting with :wastebasket: will delete a given amount of messages except the pinned ones.")
                       .append("\n\nYou have **1 minute** to react.");
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
                                case Emojis.CHECK:
                                    Utils.deleteMessage(msg);
                                    break;
                                case Emojis.CROSS:
                                    Utils.deleteMessage(msg);
                                    return;
                                case wastebasket:
                                    toDelete.removeIf(Message::isPinned);
                                    if (toDelete.isEmpty())
                                    {
                                        Utils.returnError("There are no messages to be deleted", msg);
                                        return;
                                    }
                                    Utils.deleteMessage(msg);
                                    break;
                                default: break;
                            }
                            proceed(toDelete, user, channel);
                        }, 1, TimeUnit.MINUTES, () -> Utils.returnError("Sorry, you took too long", msg));
                });
            }
            else
                proceed(toDelete, user, channel);
        });
    }

    private void proceed(final List<Message> toDelete, final User user, MessageChannel channel)
    {
        final var future = CompletableFuture.allOf(channel.purgeMessages(toDelete).toArray(new CompletableFuture[0]));
        future.thenRunAsync(() ->
                channel.sendMessage(Utils.generateSuccess(toDelete.size(), user))
                        .delay(Duration.ofSeconds(5))
                        .flatMap(Message::delete)
                        .queue());
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
    public final String getUsage() { return "s!d <count> (user)"; }
}