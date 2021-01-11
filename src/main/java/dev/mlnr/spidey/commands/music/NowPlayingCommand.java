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
        super("nowplaying", new String[]{"np", "playing"}, Category.MUSIC, Permission.UNKNOWN, 0, 0);
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
        final var guildId = guild.getIdLong();
        final var paused = musicPlayer.isPaused();
        final var trackInfo = playingTrack.getInfo();
        final var progressBuilder = Utils.createEmbedBuilder(ctx.getAuthor());
        final var stream = trackInfo.isStream;
        final var position = playingTrack.getPosition();

        final var lengthWithoutSegments = MusicUtils.getLengthWithoutSegments(playingTrack, guildId);
        final var originalLength = trackInfo.length;

        final var pausedBuilder = new StringBuilder(trackInfo.title);
        if (paused)
        {
            pausedBuilder.append(" - ").append(i18n.get("commands.nowplaying.other.paused"));
            if (!stream)
                pausedBuilder.append(" ").append(i18n.get("commands.nowplaying.other.at")).append(" ").append(formatDuration(position));
        }

        progressBuilder.setAuthor(pausedBuilder.toString(), trackInfo.uri);
        progressBuilder.setThumbnail("https://i.ytimg.com/vi/" + trackInfo.identifier + "/maxresdefault.jpg");
        progressBuilder.setColor(paused ? Color.ORANGE : Color.GREEN);
        progressBuilder.setDescription(stream ? i18n.get("commands.nowplaying.other.livestream") : MusicUtils.getProgressBar(position, originalLength));
        progressBuilder.addField(i18n.get("commands.nowplaying.other.requested"), "<@" + MusicUtils.getRequesterId(playingTrack) + ">", true);

        if (lengthWithoutSegments != originalLength)
            progressBuilder.addField(i18n.get("commands.nowplaying.other.duration_without_segments"), formatDuration(lengthWithoutSegments), true);
        ctx.reply(progressBuilder);
    }
}