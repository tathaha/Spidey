package dev.mlnr.spidey.objects.command;

import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.time.Duration;
import java.util.Set;

public class CommandContext
{
    private final GuildMessageReceivedEvent event;

    public CommandContext(final GuildMessageReceivedEvent event)
    {
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

    public void reply(final EmbedBuilder embedBuilder)
    {
        Utils.sendMessage(getTextChannel(), embedBuilder.build());
    }

    public void reply(final String content)
    {
        reply(content, MessageAction.getDefaultMentions());
    }

    public void reply(final String content, final Set<Message.MentionType> allowedMentions)
    {
        Utils.sendMessage(getTextChannel(), content, allowedMentions);
    }

    public void replyError(final String error)
    {
        Utils.addReaction(getMessage(), Emojis.CROSS);
        final var channel = getTextChannel();
        if (!channel.canTalk())
            return;
        channel.sendMessage(String.format(":no_entry: %s.", error))
                .delay(Duration.ofSeconds(7))
                .flatMap(Message::delete)
                .queue(success -> Utils.deleteMessage(getMessage()));
    }

    public void reactLike()
    {
        Utils.addReaction(getMessage(), Emojis.LIKE);
    }
}