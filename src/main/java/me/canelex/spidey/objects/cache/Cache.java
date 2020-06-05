package me.canelex.spidey.objects.cache;

import me.canelex.spidey.Core;
import me.canelex.spidey.MySQL;
import me.canelex.spidey.objects.invites.WrappedInvite;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cache
{
    private static final Map<Long, String> PREFIX_CACHE = new HashMap<>();
    private static final Map<String, WrappedInvite> INVITE_CACHE = new HashMap<>();
    private static final Map<Long, Long> LOG_CHANNEL_CACHE = new HashMap<>();
    private static final Map<Long, Long> JOIN_ROLE_CACHE = new HashMap<>();

    private Cache()
    {
        super();
    }

    public static Map<String, WrappedInvite> getInviteCache()
    {
        return INVITE_CACHE;
    }

    // PREFIX CACHING

    public static String getPrefix(final long guildId)
    {
        return Objects.requireNonNullElseGet(PREFIX_CACHE.get(guildId), () -> getPrefixByRequest(guildId));
    }

    private static String getPrefixByRequest(final long guildId)
    {
        final var tmp = MySQL.getPrefix(guildId);
        final var prefix = tmp == null ? "s!" : tmp;
        PREFIX_CACHE.put(guildId, prefix);
        return prefix;
    }

    public static void setPrefix(final long guildId, final String prefix)
    {
        MySQL.setPrefix(guildId, prefix);
        PREFIX_CACHE.put(guildId, prefix);
    }

    // LOG CHANNEL CACHING

    public static long getLogChannel(final long guildId)
    {
        return Objects.requireNonNullElseGet(LOG_CHANNEL_CACHE.get(guildId), () -> getLogChannelByRequest(guildId));
    }

    public static TextChannel getLogAsChannel(final long guildId)
    {
        return Core.getJDA().getTextChannelById(getLogChannel(guildId));
    }

    private static long getLogChannelByRequest(final long guildId)
    {
        final var channel = MySQL.getChannel(guildId);
        LOG_CHANNEL_CACHE.put(guildId, channel);
        return channel;
    }

    public static void setLogChannel(final long guildId, final long channelId)
    {
        if (channelId == 0)
        {
            MySQL.removeChannel(guildId);
            LOG_CHANNEL_CACHE.put(guildId, 0L); // IJ is forcing me to type "L" after "0" although it's not necessary
            return;
        }
        MySQL.setChannel(guildId, channelId);
        LOG_CHANNEL_CACHE.put(guildId, channelId);
    }

    public static void removeLogChannel(final long guildId)
    {
        setLogChannel(guildId, 0);
    }

    // JOIN ROLE CACHING

    public static long getJoinRole(final long guildId)
    {
        return Objects.requireNonNullElseGet(JOIN_ROLE_CACHE.get(guildId), () -> getJoinRoleByRequest(guildId));
    }

    private static long getJoinRoleByRequest(final long guildId)
    {
        final var role = MySQL.getRole(guildId);
        JOIN_ROLE_CACHE.put(guildId, role);
        return role;
    }

    public static void setJoinRole(final long guildId, final long roleId)
    {
        if (roleId == 0)
        {
            MySQL.removeRole(guildId);
            JOIN_ROLE_CACHE.put(guildId, 0L);
            return;
        }
        MySQL.setRole(guildId, roleId);
        JOIN_ROLE_CACHE.put(guildId, roleId);
    }

    public static void removeJoinRole(final long guildId)
    {
        setJoinRole(guildId, 0);
    }
}