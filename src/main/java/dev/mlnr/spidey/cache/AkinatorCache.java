package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.objects.akinator.AkinatorData;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AkinatorCache
{
    private static final Map<Long, AkinatorData> AKINATOR_CACHE = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .expiration(2, TimeUnit.MINUTES)
            .build();

    private AkinatorCache() {}

    public static void cacheAkinator(final long userId, final AkinatorData akinator)
    {
        AKINATOR_CACHE.put(userId, akinator);
    }

    public static AkinatorData getAkinatorData(final long userId)
    {
        return AKINATOR_CACHE.get(userId);
    }

    public static boolean hasAkinator(final long userId)
    {
        return AKINATOR_CACHE.containsKey(userId);
    }

    public static void removeAkinator(final long userId)
    {
        AKINATOR_CACHE.remove(userId);
    }
}