package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.objects.guild.InviteData;

import java.util.HashMap;
import java.util.Map;

public class GeneralCache
{
    private static final Map<String, InviteData> INVITE_CACHE = new HashMap<>();

    private GeneralCache() {}

    public static Map<String, InviteData> getInviteCache()
    {
        return INVITE_CACHE;
    }

    public static void removeEntry(final long guildId)
    {
        if (GuildSettingsCache.isVip(guildId))
            return;
        GuildSettingsCache.remove(guildId);
        DatabaseManager.removeEntry(guildId);
    }
}
