package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.DatabaseManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PrefixCache
{
    private static final Map<Long, String> PREFIX_CACHE = new HashMap<>();

    private PrefixCache() {}

    public static String getPrefix(final long guildId)
    {
        return Objects.requireNonNullElseGet(PREFIX_CACHE.get(guildId), () ->
        {
            final var retrieved = DatabaseManager.retrievePrefix(guildId);
            final var prefix = retrieved.isEmpty() ? "s!" : retrieved;
            PREFIX_CACHE.put(guildId, prefix);
            return prefix;
        });
    }

    public static void setPrefix(final long guildId, final String prefix)
    {
        DatabaseManager.setPrefix(guildId, prefix);
        PREFIX_CACHE.put(guildId, prefix);
    }

    public static Map<Long, String> getCache()
    {
        return PREFIX_CACHE;
    }
}