package dev.mlnr.spidey.objects.akinator;

import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.objects.I18n;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AkinatorContext
{
    private final GuildMessageReceivedEvent event;

    public AkinatorContext(final GuildMessageReceivedEvent event)
    {
        this.event = event;
    }

    public Message getMessage()
    {
        return event.getMessage();
    }

    public TextChannel getChannel()
    {
        return event.getChannel();
    }

    public I18n getI18n()
    {
        return GuildSettingsCache.getI18n(event.getGuild().getIdLong());
    }
}