package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class SegmentSkippingCommand extends Command
{
    public SegmentSkippingCommand()
    {
        super("segmentskipping", new String[]{"segmentskip", "segskip", "skipping", "segskipping"}, "Enables/disables non-music segment skipping (experimental feature)", "segmentskipping",
                Category.SETTINGS, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        if (!MusicUtils.canInteract(ctx.getMember()))
        {
            ctx.replyError("You have to be a DJ to enable/disable segment skipping");
            return;
        }
        final var guildId = ctx.getGuild().getIdLong();
        final var enabled = !GuildSettingsCache.isSegmentSkippingEnabled(guildId);
        GuildSettingsCache.setSegmentSkippingEnabled(guildId, enabled);
        ctx.reactLike();
        ctx.reply("Segment skipping has been **" + (enabled ? "enabled" : "disabled") + "**." +
                (enabled ? " As SponsorBlock is a crowdsourced extension, some segments can be placed at wrong timing. Report such submissions on SponsorBlock's Discord, not to the Developer.": ""));
    }
}