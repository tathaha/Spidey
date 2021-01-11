package dev.mlnr.spidey.commands.moderation;

import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Emojis;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static dev.mlnr.spidey.utils.Utils.*;

@SuppressWarnings({"unused", "StringBufferReplaceableByString"})
public class PurgeCommand extends Command
{
    public PurgeCommand()
    {
        super("purge", new String[]{"d", "delete"}, Category.MODERATION, Permission.MESSAGE_MANAGE, 2, 6);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var guild = ctx.getGuild();
        final var i18n = ctx.getI18n();
        if (!guild.getSelfMember().hasPermission(ctx.getTextChannel(), getRequiredPermission(), Permission.MESSAGE_HISTORY))
        {
            ctx.replyError(i18n.get("commands.purge.messages.failure.no_perms"));
            return;
        }
        if (args.length == 0)
        {
            ctx.replyError(i18n.get("commands.purge.messages.failure.wrong_syntax"), GuildSettingsCache.getPrefix(guild.getIdLong()));
            return;
        }
        ctx.getArgumentAsUnsignedInt(0, amount ->
        {
            if (amount < 1 || amount > 100)
            {
                ctx.replyError(i18n.get("commands.purge.messages.failure.invalid_number"));
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
        final var i18n = ctx.getI18n();
        message.delete().queue(ignored -> channel.getIterableHistory().cache(false).limit(target == null ? limit : 100).queue(messages ->
        {
            if (messages.isEmpty())
            {
                ctx.replyError(i18n.get("commands.purge.messages.failure.no_messages.text"));
                return;
            }
            final var msgs = target == null ? messages : messages.stream().filter(msg -> msg.getAuthor().equals(target)).limit(limit).collect(Collectors.toList());
            if (msgs.isEmpty())
            {
                ctx.replyError(i18n.get("commands.purge.messages.failure.no_messages.user", target.getAsTag()));
                return;
            }
            final var pinned = msgs.stream().filter(Message::isPinned).collect(Collectors.toList());
            if (pinned.isEmpty())
            {
                proceed(msgs, target, ctx);
                return;
            }
            final var size = pinned.size();
            final var builder = new StringBuilder(size == 1 ? i18n.get("commands.purge.messages.pinned.one")
                    : i18n.get("commands.purge.messages.pinned.multiple"));
            builder.append(i18n.get("commands.purge.messages.pinned.confirmation.text")).append(" ");
            builder.append(size == 1 ? i18n.get("commands.purge.messages.pinned.confirmation.one")
                    : i18n.get("commands.purge.messages.pinned.confirmation.multiple"));
            builder.append("?").append(i18n.get("commands.purge.messages.pinned.middle_text.text"));
            builder.append(" ").append(size == 1 ? i18n.get("commands.purge.messages.pinned.middle_text.one")
                    : i18n.get("commands.purge.messages.pinned.middle_text_multiple"));
            builder.append(i18n.get("commands.purge.messages.pinned.end_text"));

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
                                        ctx.replyError(i18n.get("commands.purge.messages.failure.no_messages.unpinned"));
                                        return;
                                    }
                                    break;
                                default:
                            }
                            proceed(msgs, target, ctx);
                        }, 1, TimeUnit.MINUTES, () -> ctx.replyError(i18n.get("took_too_long")));
            });
        }, throwable -> ctx.replyError(i18n.get("internal_error", "purge messages", throwable.getMessage()))));
    }

    private void proceed(final List<Message> toDelete, final User user, final CommandContext ctx)
    {
        final var future = purgeMessages(toDelete.toArray(new Message[0]));
        future.thenRunAsync(() -> ctx.getTextChannel().sendMessage(generateSuccess(toDelete.size(), user, ctx.getI18n()))
                .delay(Duration.ofSeconds(5))
                .flatMap(Message::delete)
                .queue());
    }
}