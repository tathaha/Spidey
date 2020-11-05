package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.cache.music.VideoSegmentCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

import static dev.mlnr.spidey.utils.MusicUtils.formatDuration;

@SuppressWarnings("unused")
public class SegmentsCommand extends Command
{
    public SegmentsCommand()
    {
        super("segments", new String[]{"segs"}, "Lists all non music segments in this video", "segments (force/reload)", Category.MUSIC, Permission.UNKNOWN, 0, 0);
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
        final var videoId = playingTrack.getIdentifier();
        var segments = VideoSegmentCache.getVideoSegments(videoId);
        if (args.length > 0)
        {
            if (!(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("force")))
            {
                ctx.replyError("Valid arguments are **force** or **reload**");
                return;
            }
            segments = VideoSegmentCache.getVideoSegments(videoId, true);
        }
        final var updatePrompt = "If you've just added some segments, force the cache to update by using `" + GuildSettingsCache.getPrefix(guild.getIdLong()) + "segments force`.";
        if (segments == null)
        {
            ctx.replyError("There are no segments in this video. " + updatePrompt);
            return;
        }
        final var stringBuilder = new StringBuilder("There are **" + segments.size() + "** segments in this video!\n\n");
        segments.forEach((segmentStart, segmentEnd) -> stringBuilder.append("**").append(formatDuration(segmentStart)).append("**").append(" - ").append("**").append(formatDuration(segmentEnd)).append("**\n"));
        stringBuilder.append("\n").append(updatePrompt);
        ctx.reply(stringBuilder.toString());
    }
}