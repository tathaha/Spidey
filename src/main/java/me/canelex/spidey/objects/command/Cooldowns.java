package me.canelex.spidey.objects.command;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.canelex.spidey.Core;
import me.canelex.spidey.objects.cache.Cache;

import java.util.concurrent.TimeUnit;

public class Cooldowns
{
    private static final Multimap<Long, Command> COOLDOWN_MAP = ArrayListMultimap.create();

    private Cooldowns()
    {
        super();
    }

    public static void cooldown(final long guildId, final Command cmd)
    {
        final var cooldown = cmd.getCooldown();
        if (cooldown == 0 || Cache.isSupporter(guildId))
            return;
        COOLDOWN_MAP.put(guildId, cmd);
        Core.getExecutor().schedule(() -> COOLDOWN_MAP.remove(guildId, cmd), Cache.getCooldown(guildId, cmd), TimeUnit.SECONDS);
    }

    public static boolean isOnCooldown(final long guildId, final Command cmd)
    {
        return COOLDOWN_MAP.containsEntry(guildId, cmd);
    }
}