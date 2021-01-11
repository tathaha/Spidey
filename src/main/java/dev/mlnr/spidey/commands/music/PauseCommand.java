package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.MusicUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class PauseCommand extends Command
{
    public PauseCommand()
    {
        super("pause", new String[]{"unpause"}, Category.MUSIC, Permission.UNKNOWN, 0, 0);
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
        final var playingTrack = musicPlayer.getPlayingTrack();
        if (playingTrack == null)
        {
            ctx.replyError(i18n.get("music.messages.failure.no_song"));
            return;
        }
        if (!MusicUtils.canInteract(ctx.getMember(), playingTrack))
        {
            ctx.replyError(i18n.get("commands.pause.other.requester"));
            return;
        }
        final var paused = musicPlayer.pauseOrUnpause();
        Utils.addReaction(ctx.getMessage(), paused ? "\u23F8\uFE0F" : Emojis.FORWARD);
    }
}