package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.MusicUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.awt.*;

import static dev.mlnr.spidey.utils.MusicUtils.formatDuration;

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
        final var guildId = guild.getIdLong();
        final var paused = musicPlayer.isPaused();
        final var trackInfo = playingTrack.getInfo();
        final var progressBuilder = Utils.createEmbedBuilder(ctx.getAuthor());
        final var stream = trackInfo.isStream;
        final var position = playingTrack.getPosition();

        final var lengthWithoutSegments = MusicUtils.getLengthWithoutSegments(playingTrack, guildId);
        final var originalLength = trackInfo.length;

        progressBuilder.setAuthor(trackInfo.title + (paused ? " - Paused" + (stream ? "" : " at " + formatDuration(position)) : ""), trackInfo.uri);
        progressBuilder.setThumbnail("https://i.ytimg.com/vi/" + trackInfo.identifier + "/maxresdefault.jpg");
        progressBuilder.setColor(paused ? Color.ORANGE : Color.GREEN);
        progressBuilder.setDescription(stream ? "Livestream" : MusicUtils.getProgressBar(position, originalLength));
        progressBuilder.addField("Requested by", guild.getMemberById(MusicUtils.getRequesterId(playingTrack)).getEffectiveName(), true);

        if (lengthWithoutSegments != originalLength)
            progressBuilder.addField("Duration without segments", formatDuration(lengthWithoutSegments), false);
        ctx.reply(progressBuilder);
    }
}