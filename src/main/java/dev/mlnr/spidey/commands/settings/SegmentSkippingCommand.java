package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.cache.settings.GuildSettingsCache;
import dev.mlnr.spidey.cache.settings.UserSettingsCache;
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
                Category.SETTINGS, Permission.UNKNOWN, 0, 4);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var canInteract = MusicUtils.canInteract(ctx.getMember());
        var enabled = false;
        if (canInteract)
        {
            final var guildId = ctx.getGuild().getIdLong();
            enabled = !GuildSettingsCache.isSegmentSkippingEnabled(guildId);
            GuildSettingsCache.setSegmentSkippingEnabled(guildId, enabled);
        }
        else
        {
            final var userId = ctx.getAuthor().getIdLong();
            enabled = !UserSettingsCache.isSegmentSkippingEnabled(userId);
            UserSettingsCache.setSegmentSkippingEnabled(userId, enabled);
        }
        ctx.reactLike();
        ctx.reply("Segment skipping has been **" + (enabled ? "enabled" : "disabled") + "**" + (canInteract ? "." : " for you. Songs that you request won't skip non music segments.") +
                (enabled ? " As SponsorBlock is a crowdsourced extension, some segments can be placed at wrong timing. Report such submissions on SponsorBlock's Discord, not to the Developer.": ""));
    }
}