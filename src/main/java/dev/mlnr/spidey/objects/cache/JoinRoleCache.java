package dev.mlnr.spidey.objects.cache;

import dev.mlnr.spidey.DatabaseManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JoinRoleCache
{
    private static final Map<Long, Long> JOIN_ROLE_CACHE = new HashMap<>();

    private JoinRoleCache() {}

    public static long retrieveJoinRole(final long guildId)
    {
        return Objects.requireNonNullElseGet(JOIN_ROLE_CACHE.get(guildId), () ->
        {
            final var role = DatabaseManager.retrieveRole(guildId);
            JOIN_ROLE_CACHE.put(guildId, role);
            return role;
        });
    }

    public static Role getJoinRole(final long guildId, final JDA jda)
    {
        return jda.getRoleById(retrieveJoinRole(guildId));
    }

    public static void setJoinRole(final long guildId, final long roleId)
    {
        JOIN_ROLE_CACHE.put(guildId, roleId);
        DatabaseManager.setRole(guildId, roleId);
    }

    public static void removeJoinRole(final long guildId)
    {
        setJoinRole(guildId, 0);
    }

    public static Map<Long, Long> getCache()
    {
        return JOIN_ROLE_CACHE;
    }
}