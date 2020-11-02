package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class PauseCommand extends Command
{
    public PauseCommand()
    {
        super("pause", new String[]{"unpause"}, "Pauses/unpauses the playback", "pause/unpause", Category.MUSIC, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var guild = ctx.getGuild();
        final var musicPlayer = MusicPlayerCache.getMusicPlayer(guild);
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
            ctx.replyError("You have to be the requester of the song or DJ to pause/unpause the playback");
            return;
        }
        musicPlayer.pauseOrUnpause();
        ctx.reactLike();
    }
}