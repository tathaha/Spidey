package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.cache.MessageCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class SnipingCommand extends Command
{
    public SnipingCommand()
    {
        super("sniping", new String[]{}, "Enables/disables message delete/edit sniping", "sniping", Category.SETTINGS, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var guildId = ctx.getGuild().getIdLong();
        final var enabled = !GuildSettingsCache.isSnipingEnabled(guildId);
        GuildSettingsCache.setSnipingEnabled(guildId, enabled);
        ctx.reactLike();
        ctx.reply("Message sniping has been **" + (enabled ? "enabled" : "disabled") + "**.");
        if (!enabled)
            MessageCache.pruneCache(guildId);
    }
}