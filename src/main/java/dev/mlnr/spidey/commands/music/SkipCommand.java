package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.cache.MusicPlayerCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

public class SkipCommand extends Command
{
    public SkipCommand()
    {
        super("skip", new String[]{}, "Skips a song", "skip", Category.MUSIC, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var musicPlayer = MusicPlayerCache.getMusicPlayer(ctx.getGuild());
        if (musicPlayer == null)
        {
            ctx.replyError("There is no music playing");
            return;
        }
        final var playingTrack = musicPlayer.getPlayingTrack();
        if (playingTrack == null)
        {
            ctx.replyError("There is no song playing");
            return;
        }
        if (!MusicUtils.canInteract(ctx.getMember(), playingTrack))
        {
            ctx.replyError("You have to be the requester of the song or DJ in order to skip this song");
            return;
        }
        musicPlayer.skip();
        ctx.reactLike();
    }
}