package me.canelex.spidey.objects.command;

import me.canelex.spidey.Core;
import me.canelex.spidey.objects.cache.Cache;
import me.canelex.spidey.utils.collections.CollectionUtils;

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
        if (cooldown == 0 || Cache.isSupporter(guildId))
            return;
        CollectionUtils.add(COOLDOWN_MAP, guildId, cmd);
        Core.getExecutor().schedule(() -> CollectionUtils.remove(COOLDOWN_MAP, guildId, cmd), Cache.getCooldown(guildId, cmd), TimeUnit.SECONDS);
    }

    public static boolean isOnCooldown(final long guildId, final Command cmd)
    {
        return CollectionUtils.contains(COOLDOWN_MAP, guildId, cmd);
    }
}