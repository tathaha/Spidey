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
		super("nowplaying", "Shows what the current song is", Category.MUSIC, Permission.UNKNOWN, 0);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var guild = ctx.getGuild();
		var musicPlayer = ctx.getCache().getMusicPlayerCache().getMusicPlayer(guild);
		var i18n = ctx.getI18n();
		if (musicPlayer == null) {
			ctx.replyErrorLocalized("music.messages.failure.no_music");
			return false;
		}
		var playingTrack = musicPlayer.getPlayingTrack();
		if (playingTrack == null) {
			ctx.replyLocalized("music.messages.failure.no_song");
			return false;
		}
		var guildId = guild.getIdLong();
		var paused = musicPlayer.isPaused();
		var trackInfo = playingTrack.getInfo();
		var progressBuilder = Utils.createEmbedBuilder(ctx.getUser());
		var stream = trackInfo.isStream;
		var position = playingTrack.getPosition();

		var lengthWithoutSegments = MusicUtils.getLengthWithoutSegments(playingTrack, guildId);
		var originalLength = trackInfo.length;

		var pausedBuilder = new StringBuilder(trackInfo.title);
		if (paused) {
			pausedBuilder.append(" - ").append(i18n.get("commands.nowplaying.paused"));
			if (!stream) {
				pausedBuilder.append(" ").append(i18n.get("commands.nowplaying.at")).append(" ").append(formatDuration(position));
			}
		}

		progressBuilder.setAuthor(pausedBuilder.toString(), trackInfo.uri);
		progressBuilder.setThumbnail(MusicUtils.getArtworkUrl(playingTrack));
		progressBuilder.setColor(paused ? Color.ORANGE.getRGB() : Utils.SPIDEY_COLOR);
		progressBuilder.setDescription(stream ? i18n.get("commands.nowplaying.livestream") : MusicUtils.getProgressBar(position, originalLength));
		progressBuilder.addField(i18n.get("commands.nowplaying.requested"), "<@" + MusicUtils.getRequesterId(playingTrack) + ">", true);

		if (lengthWithoutSegments != originalLength) {
			progressBuilder.addField(i18n.get("commands.nowplaying.duration_without_segments"), formatDuration(lengthWithoutSegments), true);
		}
		ctx.reply(progressBuilder);
		return true;
	}
}