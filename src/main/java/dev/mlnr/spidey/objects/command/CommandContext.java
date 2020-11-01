package dev.mlnr.spidey.objects.command;

import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.Duration;

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

    public void reply(final String content)
    {
        Utils.sendMessage(getTextChannel(), content);
    }

    public void reply(final EmbedBuilder embedBuilder)
    {
        Utils.sendMessage(getTextChannel(), embedBuilder.build());
    }

    public void replyError(final String error)
    {
        Utils.addReaction(getMessage(), Emojis.CROSS);
        final var channel = getTextChannel();
        if (!channel.canTalk())
            return;
        channel.sendMessage(String.format(":no_entry: %s.", error))
                .delay(Duration.ofSeconds(5))
                .flatMap(Message::delete)
                .queue(success -> Utils.deleteMessage(getMessage()));
    }
}