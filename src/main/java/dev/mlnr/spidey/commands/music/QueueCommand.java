package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.StringUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class QueueCommand extends Command
{
    public QueueCommand()
    {
        super("queue", new String[]{"q"}, Category.MUSIC, Permission.UNKNOWN, 0, 3);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var guild = ctx.getGuild();
        final var musicPlayer = MusicPlayerCache.getMusicPlayer(guild);
        final var i18n = ctx.getI18n();
        if (musicPlayer == null)
        {
            ctx.replyError(i18n.get("music.messages.failure.no_music"));
            return;
        }
        final var trackScheduler = musicPlayer.getTrackScheduler();
        final var queue = trackScheduler.getQueue();
        if (queue.isEmpty())
        {
            ctx.replyError(i18n.get("commands.queue.other.empty"));
            return;
        }
        StringUtils.createQueuePaginator(ctx.getMessage(), queue, i18n);
    }
}