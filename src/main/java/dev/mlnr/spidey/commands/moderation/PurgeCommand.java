package dev.mlnr.spidey.commands.moderation;

import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.cache.settings.GuildSettingsCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Emojis;
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
        super("purge", new String[]{"d", "delete"}, "Purges messages (by entered user)", "purge <count> (@user, user id or username/nickname)", Category.MODERATION, Permission.MESSAGE_MANAGE, 2, 6);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        if (!ctx.getGuild().getSelfMember().hasPermission(ctx.getTextChannel(), getRequiredPermission(), Permission.MESSAGE_HISTORY))
        {
            ctx.replyError("I don't have permission to purge messages or see the message history in this channel");
            return;
        }
        if (args.length == 0)
        {
            ctx.replyError("Wrong syntax. Use `" + GuildSettingsCache.getPrefix(ctx.getGuild().getIdLong()) + "help purge` to see the proper syntax");
            return;
        }
        ctx.getArgumentAsUnsignedInt(0).ifPresent(amount ->
        {
            if (amount < 1 || amount > 100)
            {
                ctx.replyError("Please enter a number from 1-100");
                return;
            }
            if (args.length == 1)
            {
                respond(ctx, null, amount);
                return;
            }
            ctx.getArgumentAsUser(1, user -> respond(ctx, user, amount));
        });
    }

    private void respond(final CommandContext ctx, final User target, final int limit)
    {
        final var message = ctx.getMessage();
        final var channel = ctx.getTextChannel();
        message.delete().queue(ignored -> channel.getIterableHistory().cache(false).limit(target == null ? limit : 100).queue(messages ->
        {
            if (messages.isEmpty())
            {
                ctx.replyError("There are no messages to be deleted");
                return;
            }
            final var msgs = target == null ? messages : messages.stream().filter(msg -> msg.getAuthor().equals(target)).limit(limit).collect(Collectors.toList());
            if (msgs.isEmpty())
            {
                ctx.replyError("There are no messages by user **" + target.getAsTag() + "** to be deleted");
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
            channel.sendMessage(builder.toString()).queue(sentMessage ->
            {
                final var wastebasket = "\uD83D\uDDD1";
                addReaction(sentMessage, Emojis.CHECK);
                addReaction(sentMessage, wastebasket);
                addReaction(sentMessage, Emojis.CROSS);

                Spidey.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class,
                        ev -> ev.getUser() == ctx.getAuthor() && ev.getMessageIdLong() == sentMessage.getIdLong(),
                        ev ->
                        {
                            switch (ev.getReactionEmote().getName())
                            {
                                case Emojis.CHECK:
                                    deleteMessage(sentMessage);
                                    break;
                                case Emojis.CROSS:
                                    deleteMessage(sentMessage);
                                    return;
                                case wastebasket:
                                    msgs.removeAll(pinned);
                                    deleteMessage(sentMessage);
                                    if (msgs.isEmpty())
                                    {
                                        ctx.replyError("There are no unpinned messages to be deleted");
                                        return;
                                    }
                                    break;
                                default:
                            }
                            proceed(msgs, target, channel);
                        }, 1, TimeUnit.MINUTES, () -> ctx.replyError("Sorry, you took too long"));
            });
        }, throwable -> ctx.replyError("Unfortunately, i couldn't purge messages due to an internal error: **" + throwable.getMessage() + "**. Please report this message to the Developer")));
    }

    private void proceed(final List<Message> toDelete, final User user, final TextChannel channel)
    {
        final var future = CompletableFuture.allOf(channel.purgeMessages(toDelete).toArray(new CompletableFuture[0]));
        future.thenRunAsync(() -> channel.sendMessage(generateSuccess(toDelete.size(), user))
                .delay(Duration.ofSeconds(5))
                .flatMap(Message::delete)
                .queue());
    }
}