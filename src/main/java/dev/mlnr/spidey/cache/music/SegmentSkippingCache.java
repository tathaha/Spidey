package dev.mlnr.spidey.cache.music;

import dev.mlnr.spidey.DatabaseManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SegmentSkippingCache
{
    private static final Map<Long, Boolean> SKIPPING_CACHE = new HashMap<>();

    private SegmentSkippingCache() {}

    public static boolean isSkippingEnabled(final long guildId)
    {
        return Objects.requireNonNullElseGet(SKIPPING_CACHE.get(guildId), () ->
        {
            final var enabled = DatabaseManager.retrieveSkippingEnabled(guildId);
            SKIPPING_CACHE.put(guildId, enabled);
            return enabled;
        });
    }

    public static void setSkippingEnabled(final long guildId, final boolean enabled)
    {
        SKIPPING_CACHE.put(guildId, enabled);
        DatabaseManager.setSkippingEnabled(guildId, enabled);
    }

    public static Map<Long, Boolean> getCache()
    {
        return SKIPPING_CACHE;
    }
}