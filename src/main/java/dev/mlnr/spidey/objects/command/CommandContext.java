package dev.mlnr.spidey.objects.command;

import dev.mlnr.spidey.utils.ArgumentUtils;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Consumer;

public class CommandContext
{
    private final String[] args;
    private final GuildMessageReceivedEvent event;

    public CommandContext(final String[] args, final GuildMessageReceivedEvent event)
    {
        this.args = args;
        this.event = event;
    }

    public Message getMessage()
    {
        return event.getMessage();
    }

    public User getAuthor()
    {
        return event.getAuthor();
    }

    public Member getMember()
    {
        return event.getMember();
    }

    public TextChannel getTextChannel()
    {
        return getMessage().getTextChannel();
    }

    public Guild getGuild()
    {
        return event.getGuild();
    }

    public JDA getJDA()
    {
        return event.getJDA();
    }

    // interaction methods

    public void reply(final EmbedBuilder embedBuilder)
    {
        Utils.sendMessage(getTextChannel(), embedBuilder.build(), getMessage());
    }

    public void reply(final String content)
    {
        reply(content, MessageAction.getDefaultMentions());
    }

    public void reply(final String content, final Set<Message.MentionType> allowedMentions)
    {
        Utils.sendMessage(getTextChannel(), content, allowedMentions, getMessage());
    }

    public void replyError(final String error)
    {
        replyError(error, true);
    }

    public void replyError(final String error, final boolean includeDot)
    {
        replyError(error, Emojis.CROSS, includeDot);
    }

    public void replyError(final String error, final String failureEmoji)
    {
        replyError(error, failureEmoji, true);
    }

    public void replyError(final String error, final String failureEmoji, final boolean includeDot)
    {
        Utils.returnError(error, getMessage(), failureEmoji, includeDot);
    }

    public void reactLike()
    {
        Utils.addReaction(getMessage(), Emojis.LIKE);
    }

    // arg stuff

    public OptionalInt getArgumentAsUnsignedInt(final int argIndex)
    {
        return ArgumentUtils.parseArgumentAsUnsignedInt(args[argIndex], this);
    }

    public void getArgumentAsUser(final int argIndex, final Consumer<User> consumer)
    {
        ArgumentUtils.retrieveUser(args[argIndex], this, consumer);
    }
}