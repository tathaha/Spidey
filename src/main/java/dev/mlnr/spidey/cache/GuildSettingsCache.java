package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.objects.I18n;
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

    public static long getLogChannelId(long guildId)
    {
        return getGuildSettings(guildId).getLogChannelId();
    }

    public static long getJoinRoleId(long guildId)
    {
        return getGuildSettings(guildId).getJoinRoleId();
    }

    public static String getPrefix(long guildId)
    {
        return getGuildSettings(guildId).getPrefix();
    }

    public static I18n getI18n(long guildId)
    {
        return getGuildSettings(guildId).getI18n();
    }

    public static boolean isSnipingEnabled(long guildId)
    {
        return getGuildSettings(guildId).isSnipingEnabled();
    }

    public static boolean isVip(long guildId)
    {
        return getGuildSettings(guildId).isVip();
    }

    // music getters

    public static long getDJRoleId(long guildId)
    {
        return getGuildSettings(guildId).getDjRoleId();
    }

    public static boolean isSegmentSkippingEnabled(long guildId)
    {
        return getGuildSettings(guildId).isSegmentSkippingEnabled();
    }

    public static int getDefaultVolume(long guildId)
    {
        return getGuildSettings(guildId).getDefaultVolume();
    }

    // fair queue getters

    public static boolean isFairQueueEnabled(long guildId)
    {
        return getGuildSettings(guildId).isFairQueueEnabled();
    }

    public static int getFairQueueThreshold(long guildId)
    {
        return getGuildSettings(guildId).getFairQueueThreshold();
    }

    // setters

    public static void setLogChannelId(long guildId, long logChannelId)
    {
        getGuildSettings(guildId).setLogChannelId(logChannelId);
    }

    public static void setJoinRoleId(long guildId, long joinRoleId)
    {
        getGuildSettings(guildId).setJoinRoleId(joinRoleId);
    }

    public static void setPrefix(long guildId, String prefix)
    {
        getGuildSettings(guildId).setPrefix(prefix);
    }

    public static void setLanguage(long guildId, String language)
    {
        getGuildSettings(guildId).setLanguage(language);
    }

    public static void setSnipingEnabled(long guildId, boolean enabled)
    {
        getGuildSettings(guildId).setSnipingEnabled(enabled);
    }

    public static void setVip(long guildId, boolean vip)
    {
        getGuildSettings(guildId).setVip(vip);
    }

    // music setters

    public static void setDJRoleId(long guildId, long djRoleId)
    {
        getGuildSettings(guildId).setDjRoleId(djRoleId);
    }

    public static void setSegmentSkippingEnabled(long guildId, boolean enabled)
    {
        getGuildSettings(guildId).setSegmentSkippingEnabled(enabled);
    }

    public static void setDefaultVolume(long guildId, int volume)
    {
        getGuildSettings(guildId).setDefaultVolume(volume);
    }

    // fair queue setters

    public static void setFairQueueEnabled(long guildId, boolean enabled)
    {
        getGuildSettings(guildId).setFairQueueEnabled(enabled);
    }

    public static void setFairQueueThreshold(long guildId, int threshold)
    {
        getGuildSettings(guildId).setFairQueueThreshold(threshold);
    }

    // removals

    public static void removeLogChannel(long guildId)
    {
        setLogChannelId(guildId, 0);
    }

    public static void removeJoinRole(long guildId)
    {
        setJoinRoleId(guildId, 0);
    }

    public static void removeDJRole(long guildId)
    {
        setDJRoleId(guildId, 0);
    }

    // misc helpers

    public static TextChannel getLogChannel(long guildId)
    {
        var logChannelId = getLogChannelId(guildId);
        return logChannelId == 0 ? null : Spidey.getJDA().getTextChannelById(logChannelId);
    }

    public static Role getJoinRole(long guildId)
    {
        var joinRoleId = getJoinRoleId(guildId);
        return joinRoleId == 0 ? null : Spidey.getJDA().getRoleById(joinRoleId);
    }

    public static Role getDJRole(long guildId)
    {
        var djRoleId = getDJRoleId(guildId);
        return djRoleId == 0 ? null : Spidey.getJDA().getRoleById(djRoleId);
    }

    // other

    public static void remove(long guildId)
    {
        GUILD_SETTINGS_CACHE.remove(guildId);
    }

    private static GuildSettings getGuildSettings(long guildId)
    {
        return GUILD_SETTINGS_CACHE.computeIfAbsent(guildId, k -> DatabaseManager.retrieveGuildSettings(guildId));
    }
}