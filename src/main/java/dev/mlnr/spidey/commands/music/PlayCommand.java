package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.music.AudioLoader;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class PlayCommand extends Command
{
    public PlayCommand()
    {
        super("play", new String[]{"p"}, "Plays/queues a song", "play <link or search term>", Category.MUSIC, Permission.UNKNOWN, 1, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        if (args.length == 0)
        {
            ctx.replyError("Please enter a link or a search term");
            return;
        }
        final var connectionFailure = MusicUtils.checkVoiceChannel(ctx);
        if (connectionFailure != null)
        {
            ctx.replyError("I can't play music as " + connectionFailure.getReason());
            return;
        }
        var query = args[0];
        if (!MusicUtils.YOUTUBE_URL_PATTERN.matcher(args[0]).matches())
            query = "ytsearch:" + query;

        final var musicPlayer = MusicPlayerCache.getMusicPlayer(ctx.getGuild(), true);
        final var scheduler = musicPlayer.getTrackScheduler();

        if (scheduler.getQueue().isEmpty())
            scheduler.setRepeatMode(null);

        final var loader = new AudioLoader(musicPlayer, query, ctx, false);
        MusicUtils.getAudioPlayerManager().loadItemOrdered(musicPlayer, query, loader);
    }
}