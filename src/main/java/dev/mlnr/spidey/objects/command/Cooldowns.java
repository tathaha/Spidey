package dev.mlnr.spidey.objects.command;

import dev.mlnr.spidey.Core;
import dev.mlnr.spidey.objects.cache.Cache;
import dev.mlnr.spidey.utils.collections.CollectionUtils;

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
        CollectionUtils.add(COOLDOWN_MAP, guildId, cmd);
        Core.getExecutor().schedule(() -> CollectionUtils.remove(COOLDOWN_MAP, guildId, cmd), getCooldown(guildId, cmd), TimeUnit.SECONDS);
    }

    public static boolean isOnCooldown(final long guildId, final Command cmd)
    {
        return CollectionUtils.contains(COOLDOWN_MAP, guildId, cmd);
    }

    public static int getCooldown(final long guildId, final Command cmd)
    {
        final var cooldown = cmd.getCooldown();
        if (Cache.isVip(guildId))
            return cooldown / 2;
        return cooldown;
    }
}