package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.objects.guild.GuildSettings;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class GuildSettingsCache
{
    private static final Map<Long, GuildSettings> GUILD_SETTINGS_CACHE = new HashMap<>();

    private GuildSettingsCache() {}

    // getters

    public static long getLogChannelId(final long guildId)
    {
        return getGuildSettings(guildId).getLogChannelId();
    }

    public static long getJoinRoleId(final long guildId)
    {
        return getGuildSettings(guildId).getJoinRoleId();
    }

    public static String getPrefix(final long guildId)
    {
        return getGuildSettings(guildId).getPrefix();
    }

    public static boolean isSnipingEnabled(final long guildId)
    {
        return getGuildSettings(guildId).isSnipingEnabled();
    }

    public static boolean isVip(final long guildId)
    {
        return getGuildSettings(guildId).isVip();
    }

    // music getters

    public static long getDJRoleId(final long guildId)
    {
        return getGuildSettings(guildId).getDjRoleId();
    }

    public static boolean isSegmentSkippingEnabled(final long guildId)
    {
        return getGuildSettings(guildId).isSegmentSkippingEnabled();
    }

    public static int getDefaultVolume(final long guildId)
    {
        return getGuildSettings(guildId).getDefaultVolume();
    }

    // fair queue getters

    public static boolean isFairQueueEnabled(final long guildId)
    {
        return getGuildSettings(guildId).isFairQueueEnabled();
    }

    public static int getFairQueueThreshold(final long guildId)
    {
        return getGuildSettings(guildId).getFairQueueThreshold();
    }

    // setters

    public static void setLogChannelId(final long guildId, final long logChannelId)
    {
        getGuildSettings(guildId).setLogChannelId(logChannelId);
    }

    public static void setJoinRoleId(final long guildId, final long joinRoleId)
    {
        getGuildSettings(guildId).setJoinRoleId(joinRoleId);
    }

    public static void setPrefix(final long guildId, final String prefix)
    {
        getGuildSettings(guildId).setPrefix(prefix);
    }

    public static void setSnipingEnabled(final long guildId, final boolean enabled)
    {
        getGuildSettings(guildId).setSnipingEnabled(enabled);
    }

    public static void setVip(final long guildId, final boolean vip)
    {
        getGuildSettings(guildId).setVip(vip);
    }

    // music setters

    public static void setDJRoleId(final long guildId, final long djRoleId)
    {
        getGuildSettings(guildId).setDjRoleId(djRoleId);
    }

    public static void setSegmentSkippingEnabled(final long guildId, final boolean enabled)
    {
        getGuildSettings(guildId).setSegmentSkippingEnabled(enabled);
    }

    public static void setDefaultVolume(final long guildId, final int volume)
    {
        getGuildSettings(guildId).setDefaultVolume(volume);
    }

    // fair queue setters

    public static void setFairQueueEnabled(final long guildId, final boolean enabled)
    {
        getGuildSettings(guildId).setFairQueueEnabled(enabled);
    }

    public static void setFairQueueThreshold(final long guildId, final int threshold)
    {
        getGuildSettings(guildId).setFairQueueThreshold(threshold);
    }

    // removals

    public static void removeLogChannel(final long guildId)
    {
        setLogChannelId(guildId, 0);
    }

    public static void removeJoinRole(final long guildId)
    {
        setJoinRoleId(guildId, 0);
    }

    public static void removeDJRole(final long guildId)
    {
        setDJRoleId(guildId, 0);
    }

    // misc helpers

    public static TextChannel getLogChannel(final long guildId)
    {
        final var logChannelId = getLogChannelId(guildId);
        return logChannelId == 0 ? null : Spidey.getJDA().getTextChannelById(logChannelId);
    }

    public static Role getJoinRole(final long guildId)
    {
        final var joinRoleId = getJoinRoleId(guildId);
        return joinRoleId == 0 ? null : Spidey.getJDA().getRoleById(joinRoleId);
    }

    public static Role getDJRole(final long guildId)
    {
        final var djRoleId = getDJRoleId(guildId);
        return djRoleId == 0 ? null : Spidey.getJDA().getRoleById(djRoleId);
    }

    // other

    public static void remove(final long guildId)
    {
        GUILD_SETTINGS_CACHE.remove(guildId);
    }

    private static GuildSettings getGuildSettings(final long guildId)
    {
        return GUILD_SETTINGS_CACHE.computeIfAbsent(guildId, k ->
        {
            final var settings = DatabaseManager.retrieveGuildSettings(guildId);
            GUILD_SETTINGS_CACHE.put(guildId, settings);
            return settings;
        });
    }
}