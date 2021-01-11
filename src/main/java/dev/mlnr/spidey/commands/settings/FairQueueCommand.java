package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

import static dev.mlnr.spidey.cache.GuildSettingsCache.isFairQueueEnabled;

@SuppressWarnings("unused")
public class FairQueueCommand extends Command
{
    public FairQueueCommand()
    {
        super("fairqueue", new String[]{"fq"}, Category.SETTINGS, Permission.UNKNOWN, 0, 4);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var i18n = ctx.getI18n();
        if (!MusicUtils.canInteract(ctx.getMember()))
        {
            ctx.replyError(i18n.get("music.messages.failure.cant_interact", "enable/disable the fair queue or to set the threshold"));
            return;
        }
        final var guildId = ctx.getGuild().getIdLong();
        if (args.length == 0)
        {
            manageFairQueue(guildId, ctx, !isFairQueueEnabled(guildId));
            return;
        }
        ctx.getArgumentAsUnsignedInt(0, threshold ->
        {
            if (threshold == 0)
            {
                manageFairQueue(guildId, ctx, false);
                return;
            }
            if (threshold < 2 || threshold > 10)
            {
                ctx.replyError(i18n.get("commands.fairqueue.other.threshold_number"));
                return;
            }
            if (threshold == GuildSettingsCache.getFairQueueThreshold(guildId))
            {
                ctx.replyError(i18n.get("commands.fairqueue.other.already_set", threshold));
                return;
            }
            manageFairQueue(guildId, ctx, true, threshold);
        });
    }

    private void manageFairQueue(final long guildId, final CommandContext ctx, final boolean enabled)
    {
        manageFairQueue(guildId, ctx, enabled, -1);
    }

    private void manageFairQueue(final long guildId, final CommandContext ctx, final boolean enabled, final int threshold)
    {
        final var i18n = ctx.getI18n();
        if (!enabled && !isFairQueueEnabled(guildId))
        {
            ctx.replyError(i18n.get("commands.fairqueue.other.already_disabled"));
            return;
        }
        if (threshold != -1)
            GuildSettingsCache.setFairQueueThreshold(guildId, threshold);
        GuildSettingsCache.setFairQueueEnabled(guildId, enabled);
        ctx.reactLike();
        ctx.reply(i18n.get("commands.fairqueue.other.done.text", enabled ? "enabled" : "disabled") +
                (threshold == -1 ? "." : " " + i18n.get("commands.fairqueue.other.done.threshold", threshold)));
    }
}