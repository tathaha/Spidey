package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;

import static dev.mlnr.spidey.utils.MusicUtils.formatDuration;

@SuppressWarnings("unused")
public class SegmentsCommand extends Command {

	public SegmentsCommand() {
		super("segments", new String[]{"segs"}, Category.MUSIC, Permission.UNKNOWN, 0, 4);
	}

	@Override
	public boolean execute(String[] args, CommandContext ctx) {
		var cache = ctx.getCache();
		var guild = ctx.getGuild();
		var musicPlayer = cache.getMusicPlayerCache().getMusicPlayer(guild);
		var i18n = ctx.getI18n();
		if (musicPlayer == null) {
			ctx.replyErrorLocalized("music.messages.failure.no_music");
			return false;
		}
		var playingTrack = musicPlayer.getPlayingTrack();
		if (playingTrack == null) {
			ctx.replyErrorLocalized("music.messages.failure.no_song");
			return false;
		}
		var videoSegmentCache = cache.getVideoSegmentCache();
		var videoId = playingTrack.getIdentifier();
		var segments = videoSegmentCache.getVideoSegments(videoId);
		if (args.length > 0) {
			if (!(args[0].toLowerCase().startsWith("rel") || args[0].equalsIgnoreCase("force"))) {
				ctx.replyErrorLocalized("commands.segments.other.args");
				return false;
			}
			segments = videoSegmentCache.getVideoSegments(videoId, true);
		}
		var updatePrompt = i18n.get("commands.segments.other.prompt", cache.getGuildSettingsCache().getMiscSettings(guild.getIdLong()).getPrefix());
		if (segments.isEmpty()) {
			ctx.reply(i18n.get("commands.segments.other.no_segs") + " " + updatePrompt);
			return true;
		}
		var size = segments.size();
		var stringBuilder = new StringBuilder(size == 1 ? i18n.get("commands.segments.other.message.one") : i18n.get("commands.segments.other.message.multiple", size))
				.append(" ").append(i18n.get("commands.segments.other.message.video"));

		segments.forEach(segment -> stringBuilder.append("**").append(formatDuration(segment.getSegmentStart())).append("**").append(" - ").append("**")
				.append(formatDuration(segment.getSegmentEnd())).append("**\n"));
		stringBuilder.append("\n").append(updatePrompt);
		ctx.reply(stringBuilder.toString());
		return true;
	}
}