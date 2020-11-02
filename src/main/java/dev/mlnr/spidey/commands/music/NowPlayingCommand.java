package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.MusicUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.awt.*;

@SuppressWarnings("unused")
public class NowPlayingCommand extends Command
{
    public NowPlayingCommand()
    {
        super("nowplaying", new String[]{"np", "playing"}, "Shows what's the current song", "nowplaying", Category.MUSIC, Permission.UNKNOWN, 0, 0);
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
        final var paused = musicPlayer.isPaused();
        final var position = playingTrack.getPosition();
        final var duration = playingTrack.getDuration();
        final var trackInfo = playingTrack.getInfo();
        final var progressBuilder = Utils.createEmbedBuilder(ctx.getAuthor());

        progressBuilder.setAuthor(trackInfo.title + (paused ? " - Paused at " + MusicUtils.formatDuration(position) : ""), trackInfo.uri);
        progressBuilder.setThumbnail("https://i.ytimg.com/vi/" + trackInfo.identifier + "/maxresdefault.jpg");
        progressBuilder.setColor(paused ? Color.ORANGE : Color.GREEN);
        progressBuilder.addField("Author", trackInfo.author, true);
        progressBuilder.setDescription(MusicUtils.getProgressBar(position, duration));

        ctx.reply(progressBuilder);
    }
}