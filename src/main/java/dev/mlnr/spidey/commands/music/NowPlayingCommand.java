package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.awt.*;

import static dev.mlnr.spidey.utils.MusicUtils.formatDuration;

@SuppressWarnings("unused")
public class NowPlayingCommand extends Command {

	public NowPlayingCommand() {
		super("nowplaying", new String[]{"np", "playing"}, Category.MUSIC, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var guild = ctx.getGuild();
		var musicPlayer = ctx.getCache().getMusicPlayerCache().getMusicPlayer(guild);
		var i18n = ctx.getI18n();
		if (musicPlayer == null) {
			ctx.replyError(i18n.get("music.messages.failure.no_music"));
			return;
		}
		var playingTrack = musicPlayer.getPlayingTrack();
		if (playingTrack == null) {
			ctx.replyError(i18n.get("music.messages.failure.no_song"));
			return;
		}
		var guildId = guild.getIdLong();
		var paused = musicPlayer.isPaused();
		var trackInfo = playingTrack.getInfo();
		var progressBuilder = Utils.createEmbedBuilder(ctx.getAuthor());
		var stream = trackInfo.isStream;
		var position = playingTrack.getPosition();

		var lengthWithoutSegments = MusicUtils.getLengthWithoutSegments(playingTrack, guildId);
		var originalLength = trackInfo.length;

		var pausedBuilder = new StringBuilder(trackInfo.title);
		if (paused) {
			pausedBuilder.append(" - ").append(i18n.get("commands.nowplaying.other.paused"));
			if (!stream) {
				pausedBuilder.append(" ").append(i18n.get("commands.nowplaying.other.at")).append(" ").append(formatDuration(position));
			}
		}

		progressBuilder.setAuthor(pausedBuilder.toString(), trackInfo.uri);
		progressBuilder.setThumbnail("https://i.ytimg.com/vi/" + trackInfo.identifier + "/maxresdefault.jpg");
		progressBuilder.setColor(paused ? Color.ORANGE : Color.GREEN);
		progressBuilder.setDescription(stream ? i18n.get("commands.nowplaying.other.livestream") : MusicUtils.getProgressBar(position, originalLength));
		progressBuilder.addField(i18n.get("commands.nowplaying.other.requested"), "<@" + MusicUtils.getRequesterId(playingTrack) + ">", true);

		if (lengthWithoutSegments != originalLength) {
			progressBuilder.addField(i18n.get("commands.nowplaying.other.duration_without_segments"), formatDuration(lengthWithoutSegments), true);
		}
		ctx.reply(progressBuilder);
	}
}