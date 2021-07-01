package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.time.Instant;

@SuppressWarnings("unused")
public class SaveSongCommand extends Command {
	public SaveSongCommand() {
		super("savesong", "Sends the current playing song into your private messages", Category.MUSIC, Permission.UNKNOWN, 4);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var guild = ctx.getGuild();
		var musicPlayer = ctx.getCache().getMusicPlayerCache().getMusicPlayer(guild);
		if (musicPlayer == null) {
			ctx.replyErrorLocalized("music.messages.failure.no_music");
			return false;
		}
		var playingTrack = musicPlayer.getPlayingTrack();
		if (playingTrack == null) {
			ctx.replyErrorLocalized("music.messages.failure.no_song");
			return false;
		}
		var user = ctx.getUser();
		var embedBuilder = Utils.createEmbedBuilder(user).setColor(Utils.SPIDEY_COLOR);
		var trackInfo = playingTrack.getInfo();
		var length = MusicUtils.formatDuration(trackInfo.length);
		var requester = "<@" + MusicUtils.getRequesterId(playingTrack) + ">";
		embedBuilder.setAuthor("Saved song from server " + guild.getName());
		embedBuilder.setTitle(trackInfo.title, trackInfo.uri);
		embedBuilder.setThumbnail("https://i.ytimg.com/vi/" + trackInfo.identifier + "/maxresdefault.jpg");
		embedBuilder.setDescription("Length: **" + length + "**\nRequested by " + requester);
		embedBuilder.setFooter("Saved at");
		embedBuilder.setTimestamp(Instant.now());

		user.openPrivateChannel().submit()
				.thenCompose(privateChannel -> privateChannel.sendMessageEmbeds(embedBuilder.build()).submit())
				.whenComplete((ignored, throwable) -> {
					if (throwable != null) {
						ctx.replyErrorLocalized("commands.savesong.open_dms");
						return;
					}
					ctx.replyLocalized("commands.savesong.sent");
				});
		return true;
	}
}