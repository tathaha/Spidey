package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.DatabaseManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LogChannelCache
{
    private static final Map<Long, Long> LOG_CHANNEL_CACHE = new HashMap<>();

    private LogChannelCache() {}

    public static long retrieveLogChannel(final long guildId)
    {
        return Objects.requireNonNullElseGet(LOG_CHANNEL_CACHE.get(guildId), () ->
        {
            final var channel = DatabaseManager.retrieveChannel(guildId);
            LOG_CHANNEL_CACHE.put(guildId, channel);
            return channel;
        });
    }

    public static TextChannel getLogAsChannel(final long guildId, final JDA jda)
    {
        return jda.getTextChannelById(retrieveLogChannel(guildId));
    }

    public static void setLogChannel(final long guildId, final long channelId)
    {
        LOG_CHANNEL_CACHE.put(guildId, channelId);
        DatabaseManager.setChannel(guildId, channelId);
    }

    public static void removeLogChannel(final long guildId)
    {
        setLogChannel(guildId, 0);
    }

    public static Map<Long, Long> getCache()
    {
        return LOG_CHANNEL_CACHE;
    }
}