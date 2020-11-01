package dev.mlnr.spidey.objects.cache;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.objects.invites.InviteData;

import java.util.*;

public class Cache
{
    private static final Map<String, InviteData> INVITE_CACHE = new HashMap<>();
    private static final Map<Long, Boolean> VIP_GUILDS_CACHE = new HashMap<>();
    private static final Map<Long, List<String>> REDDIT_CACHE = new HashMap<>();

    private Cache()
    {
        super();
    }

    public static Map<String, InviteData> getInviteCache()
    {
        return INVITE_CACHE;
    }

    // VIP GUILDS CACHING

    public static boolean isVip(final long guildId)
    {
        return Objects.requireNonNullElseGet(VIP_GUILDS_CACHE.get(guildId), () ->
        {
            final var vip = DatabaseManager.isVip(guildId);
            VIP_GUILDS_CACHE.put(guildId, vip);
            return vip;
        });
    }

    // REDDIT POSTS CACHING

    public static boolean isPostCached(final long guildId, final String json)
    {
        final var entry = REDDIT_CACHE.get(guildId);
        return entry != null && entry.contains(json);
    }

    public static void cachePost(final long guildId, final String json)
    {
        REDDIT_CACHE.computeIfAbsent(guildId, k -> new ArrayList<>()).add(json);
    }

    // MISC

    public static void removeEntry(final long guildId)
    {
        if (isVip(guildId))
            return;
        LogChannelCache.getCache().remove(guildId);
        JoinRoleCache.getCache().remove(guildId);
        PrefixCache.getCache().remove(guildId);
        DatabaseManager.removeEntry(guildId);
    }
}
