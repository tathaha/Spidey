package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.CommandBase;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import static dev.mlnr.spidey.utils.MusicUtils.formatDuration;

@SuppressWarnings("unused")
public class SegmentsCommand extends CommandBase {
	public SegmentsCommand() {
		super("segments", "Lists all non music SponsorBlock segments in this video", Category.MUSIC, Permission.UNKNOWN, 4,
				new OptionData(OptionType.BOOLEAN, "reload", "Whether to force reload the segments"));
	}

	@Override
	public boolean execute(CommandContext ctx) {
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
		var reload = ctx.getBooleanOption("reload");
		var videoSegmentCache = cache.getVideoSegmentCache();
		var videoId = playingTrack.getIdentifier();
		var segments = videoSegmentCache.getVideoSegments(videoId);
		if (reload != null && reload) {
			segments = videoSegmentCache.getVideoSegments(videoId, true);
		}
		var updatePrompt = i18n.get("commands.segments.prompt");
		if (segments.isEmpty()) {
			ctx.reply(i18n.get("commands.segments.no_segs") + " " + updatePrompt);
			return true;
		}
		var size = segments.size();
		var stringBuilder = new StringBuilder(size == 1 ? i18n.get("commands.segments.message.one") : i18n.get("commands.segments.message.multiple", size))
				.append(" ").append(i18n.get("commands.segments.message.video"));

		segments.forEach(segment -> stringBuilder.append("**").append(formatDuration(segment.getSegmentStart())).append("**").append(" - ").append("**")
				.append(formatDuration(segment.getSegmentEnd())).append("**\n"));
		stringBuilder.append("\n").append(updatePrompt);
		ctx.reply(stringBuilder.toString());
		return true;
	}
}