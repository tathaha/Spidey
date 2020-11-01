package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.objects.invites.InviteData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GeneralCache
{
    private static final Map<String, InviteData> INVITE_CACHE = new HashMap<>();
    private static final Map<Long, Boolean> VIP_GUILDS_CACHE = new HashMap<>();

    private GeneralCache() {}

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
