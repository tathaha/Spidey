package dev.mlnr.spidey.cache.music;

import dev.mlnr.spidey.DatabaseManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DJRoleCache
{
    private static final Map<Long, Long> DJ_ROLE_CACHE = new HashMap<>();

    private DJRoleCache() {}

    public static long getDJRole(final long guildId)
    {
        return Objects.requireNonNullElseGet(DJ_ROLE_CACHE.get(guildId), () ->
        {
            final var role = DatabaseManager.retrieveDJRole(guildId);
            DJ_ROLE_CACHE.put(guildId, role);
            return role;
        });
    }

    public static Role getDJRole(final long guildId, final JDA jda)
    {
        return jda.getRoleById(getDJRole(guildId));
    }

    public static void setDJRole(final long guildId, final long roleId)
    {
        DJ_ROLE_CACHE.put(guildId, roleId);
        DatabaseManager.setDJRole(guildId, roleId);
    }

    public static void removeDJRole(final long guildId)
    {
        setDJRole(guildId, 0);
    }

    public static Map<Long, Long> getCache()
    {
        return DJ_ROLE_CACHE;
    }
}