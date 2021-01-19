package dev.mlnr.spidey.commands.music;

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
        super("play", new String[]{"p"}, Category.MUSIC, Permission.UNKNOWN, 1, 2);
    }

    @Override
    public void execute(String[] args, CommandContext ctx)
    {
        var musicPlayer = MusicUtils.checkQueryInput(args, ctx);
        if (musicPlayer == null)
            return;
        var query = MusicUtils.YOUTUBE_URL_PATTERN.matcher(args[0]).matches() ? args[0] : "ytsearch:" + args[0];
        var loader = new AudioLoader(musicPlayer, query, ctx, false);
        MusicUtils.loadQuery(musicPlayer, query, loader);
    }
}