package me.canelex.spidey.objects.command;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Cooldowns
{
    private static final Multimap<Long, Command> COOLDOWNS = ArrayListMultimap.create();
    private static final ScheduledExecutorService SES = Executors.newSingleThreadScheduledExecutor();

    private Cooldowns()
    {
        super();
    }

    public static void cooldown(final long guildId, final Command cmd)
    {
        final var cooldown = cmd.getCooldown();
        if (cooldown == 0)
            return;
        COOLDOWNS.put(guildId, cmd);
        SES.schedule(() -> COOLDOWNS.remove(guildId, cmd), cooldown, TimeUnit.SECONDS);
    }

    public static boolean isOnCooldown(final long guildId, final Command cmd)
    {
        return COOLDOWNS.containsEntry(guildId, cmd);
    }
}