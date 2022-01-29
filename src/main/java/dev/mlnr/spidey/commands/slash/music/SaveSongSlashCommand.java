package dev.mlnr.spidey.commands.slash.music;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.time.Instant;

@SuppressWarnings("unused")
public class SaveSongSlashCommand extends SlashCommand {
	public SaveSongSlashCommand() {
		super("savesong", "Sends the current playing song into your private messages", Category.MUSIC, Permission.UNKNOWN, 4);
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
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
		embedBuilder.setThumbnail(MusicUtils.getArtworkUrl(playingTrack));
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