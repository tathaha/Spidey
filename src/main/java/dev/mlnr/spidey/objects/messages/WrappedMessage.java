package dev.mlnr.spidey.objects.messages;

import net.dv8tion.jda.api.entities.Message;

import java.time.Instant;

public class WrappedMessage
{
    private final long messageId;
    private final long authorId;
    private final Instant creation;
    private final String content;
    private final long channelId;

    public WrappedMessage(final Message message)
    {
        this.messageId = message.getIdLong();
        this.authorId = message.getAuthor().getIdLong();
        this.creation = message.getTimeCreated().toInstant();
        this.content = message.getContentRaw();
        this.channelId = message.getTextChannel().getIdLong();
    }

    public long getId()
    {
        return messageId;
    }

    public long getAuthorId()
    {
        return authorId;
    }

    public Instant getCreation()
    {
        return creation;
    }

    public String getContent()
    {
        return content;
    }

    public long getChannelId()
    {
        return channelId;
    }
}