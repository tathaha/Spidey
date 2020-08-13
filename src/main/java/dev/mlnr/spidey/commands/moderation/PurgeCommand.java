package dev.mlnr.spidey.commands.moderation;

import dev.mlnr.spidey.Core;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static dev.mlnr.spidey.utils.Utils.*;

@SuppressWarnings({"unused", "StringBufferReplaceableByString"})
public class PurgeCommand extends Command
{
    public PurgeCommand()
    {
        super("purge", new String[]{"d", "delete"}, "Purges messages (by entered user)", "purge <count> (User#Discriminator, @user, user id or username/nickname)", Category.MODERATION, Permission.MESSAGE_MANAGE, 2, 6);
    }

    @Override
    public final void execute(final String[] args, final Message msg)
    {
        final var channel = msg.getTextChannel();

        if (!channel.getGuild().getSelfMember().hasPermission(channel, getRequiredPermission(), Permission.MESSAGE_HISTORY))
        {
            returnError("I don't have permission to purge messages or see the message history in this channel", msg);
            return;
        }

        if (args.length == 0)
        {
            returnError("Wrong syntax", msg);
            return;
        }

        var amount = 0;
        try
        {
            amount = Integer.parseInt(args[0]);
        }
        catch (final NumberFormatException ex)
        {
            returnError("Entered value is either negative or not a number", msg);
            return;
        }
        if (amount > 100 || amount < 1)
        {
            returnError("Please enter a number from 1-100", msg);
            return;
        }

        User user = null;
        if (args.length == 2)
        {
            final var fromArg = Utils.getUserFromArgument(args[1], channel, msg);
            if (fromArg == null)
            {
                returnError("User not found", msg);
                return;
            }
            user = fromArg;
        }
        final var limit = amount;
        final var target = user;

        msg.delete().queue(ignored -> channel.getIterableHistory().cache(false).limit(target == null ? limit : 100).queue(messages ->
        {
            if (messages.isEmpty())
            {
                returnError("There are no messages to be deleted", msg);
                return;
            }
            final var msgs = target == null ? messages : messages.stream().filter(message -> message.getAuthor().equals(target)).limit(limit).collect(Collectors.toList());
            if (msgs.isEmpty())
            {
                returnError("There are no messages by user **" + target.getAsTag() + "** to be deleted", msg);
                return;
            }
            final var pinned = msgs.stream().filter(Message::isPinned).collect(Collectors.toList());
            if (pinned.isEmpty())
            {
                proceed(msgs, target, channel);
                return;
            }
            final var size = pinned.size();
            final var builder = new StringBuilder("There ");
            builder.append(size == 1 ? "is" : "are").append(" **").append(size)
                    .append("** pinned message").append(size == 1 ? "" : "s").append(" selected for deletion. ")
                    .append("Are you sure you want to delete ").append(size == 1 ? "it" : "them").append("? ")
                    .append("Deleting a message will also unpin it.")
                    .append("\n\nReacting with :white_check_mark: will delete ").append(size == 1 ? "this message" : "these messages").append(".")
                    .append("\nReacting with :wastebasket: will delete each unpinned message.")
                    .append("\nReacting with :x: will cancel the deletion.")
                    .append("\n\nThe deletion will be cancelled automatically in **1 minute** if a decision isn't made.");
            channel.sendMessage(builder.toString()).queue(message ->
            {
                final var wastebasket = "\uD83D\uDDD1";
                addReaction(message, Emojis.CHECK);
                addReaction(message, wastebasket);
                addReaction(message, Emojis.CROSS);

                Core.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class,
                        ev -> ev.getUser() == msg.getAuthor() && ev.getMessageIdLong() == message.getIdLong(),
                        ev ->
                        {
                            switch (ev.getReactionEmote().getName())
                            {
                                case Emojis.CHECK:
                                    deleteMessage(message);
                                    break;
                                case Emojis.CROSS:
                                    deleteMessage(message);
                                    return;
                                case wastebasket:
                                    msgs.removeAll(pinned);
                                    deleteMessage(message);
                                    if (msgs.isEmpty())
                                    {
                                        returnError("There are no unpinned messages to be deleted", msg);
                                        return;
                                    }
                                    break;
                                default:
                            }
                            proceed(msgs, target, channel);
                        }, 1, TimeUnit.MINUTES, () -> returnError("Sorry, you took too long", msg));
            });
        }, throwable -> returnError("Unfortunately, i couldn't purge messages due to an internal error: **" + throwable.getMessage() + "**. Please report this message to the Developer", msg)));
    }

    private void proceed(final List<Message> toDelete, final User user, final TextChannel channel)
    {
        final var future = CompletableFuture.allOf(channel.purgeMessages(toDelete).toArray(new CompletableFuture[0]));
        future.thenRunAsync(() -> channel.sendMessage(Utils.generateSuccess(toDelete.size(), user))
                                         .delay(Duration.ofSeconds(5))
                                         .flatMap(Message::delete)
                                         .queue());
    }
}