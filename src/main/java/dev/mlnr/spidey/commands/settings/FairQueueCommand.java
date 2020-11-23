package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.cache.settings.GuildSettingsCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

import static dev.mlnr.spidey.cache.settings.GuildSettingsCache.isFairQueueEnabled;

@SuppressWarnings("unused")
public class FairQueueCommand extends Command
{
    public FairQueueCommand()
    {
        super("fairqueue", new String[]{"fq"}, "Enables/disables fair queue or sets the threshold", "fairqueue (threshold from 2 to 10; default = " + MusicUtils.MAX_FAIR_QUEUE + ")",
                Category.SETTINGS, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        if (!MusicUtils.canInteract(ctx.getMember()))
        {
            ctx.replyError("You have to be a DJ/Server Manager to enable/disable the fair queue or set the threshold");
            return;
        }
        final var guildId = ctx.getGuild().getIdLong();
        if (args.length == 0)
        {
            manageFairQueue(guildId, ctx, !isFairQueueEnabled(guildId));
            return;
        }
        var threshold = 0;
        try
        {
            threshold = Integer.parseUnsignedInt(args[0]);
        }
        catch (final NumberFormatException ex)
        {
            ctx.replyError("Please enter a valid threshold from `2` to `10`");
            return;
        }
        if (threshold == 0)
        {
            manageFairQueue(guildId, ctx, false);
            return;
        }
        if (!(threshold >= 2 && threshold <= 10))
        {
            ctx.replyError("Please enter a threshold from `2` to `10`");
            return;
        }
        if (threshold == GuildSettingsCache.getFairQueueThreshold(guildId))
        {
            ctx.replyError("The threshold is already set to **" + threshold + "**");
            return;
        }
        manageFairQueue(guildId, ctx, true, threshold);
    }

    private void manageFairQueue(final long guildId, final CommandContext ctx, final boolean enabled)
    {
        manageFairQueue(guildId, ctx, enabled, -1);
    }

    private void manageFairQueue(final long guildId, final CommandContext ctx, final boolean enabled, final int threshold)
    {
        if (!enabled && !isFairQueueEnabled(guildId))
        {
            ctx.replyError("The fair queue is already disabled");
            return;
        }
        if (threshold != -1)
            GuildSettingsCache.setFairQueueThreshold(guildId, threshold);
        GuildSettingsCache.setFairQueueEnabled(guildId, enabled);
        ctx.reactLike();
        ctx.reply("Fair queue has been **" + (enabled ? "enabled" : "disabled") + "**" + (threshold == -1 ? "." : " and the threshold has been set to **" + threshold + "**."));
    }
}