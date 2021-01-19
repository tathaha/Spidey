package dev.mlnr.spidey.handlers.command;

import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.objects.command.Command;

import java.util.HashMap;
import java.util.Map;

public class CooldownHandler
{
    private static final Map<Command, Map<Long, Long>> COOLDOWN_MAP = new HashMap<>(); // K = Command, V = Map<userId, Timestamp>

    private CooldownHandler() {}

    public static void cooldown(long guildId, long userId, Command command)
    {
        var cooldown = command.getCooldown();
        if (cooldown == 0)
            return;
        COOLDOWN_MAP.computeIfAbsent(command, k -> new HashMap<>()).put(userId, System.currentTimeMillis() + getCooldown(guildId, command) * 1000L);
    }

    public static boolean isOnCooldown(long userId, Command command)
    {
        var entry = COOLDOWN_MAP.get(command);
        if (entry == null)
            return false;
        var lastUsed = entry.get(userId);
        if (lastUsed == null)
            return false;
        if (System.currentTimeMillis() > lastUsed)
        {
            entry.remove(userId);
            return false;
        }
        return true;
    }

    public static int getCooldown(long guildId, Command command)
    {
        var cooldown = command.getCooldown();
        return GuildSettingsCache.isVip(guildId) ? cooldown / 2 : cooldown;
    }
}