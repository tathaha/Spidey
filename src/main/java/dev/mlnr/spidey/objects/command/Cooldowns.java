package dev.mlnr.spidey.objects.command;

import dev.mlnr.spidey.Core;
import dev.mlnr.spidey.objects.cache.Cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Cooldowns
{
    private static final Map<Long, List<Command>> COOLDOWN_MAP = new HashMap<>();

    private Cooldowns()
    {
        super();
    }

    public static void cooldown(final long guildId, final Command cmd)
    {
        final var cooldown = cmd.getCooldown();
        if (cooldown == 0)
            return;
        COOLDOWN_MAP.computeIfAbsent(guildId, k -> new ArrayList<>()).add(cmd);
        Core.getExecutor().schedule(() -> COOLDOWN_MAP.get(guildId).remove(cmd), getCooldown(guildId, cmd), TimeUnit.SECONDS);
    }

    public static boolean isOnCooldown(final long guildId, final Command cmd)
    {
        final var entry = COOLDOWN_MAP.get(guildId);
        return entry != null && entry.contains(cmd);
    }

    public static int getCooldown(final long guildId, final Command cmd)
    {
        final var cooldown = cmd.getCooldown();
        if (Cache.isVip(guildId))
            return cooldown / 2;
        return cooldown;
    }
}