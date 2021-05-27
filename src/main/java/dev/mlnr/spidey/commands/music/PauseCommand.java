package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class PauseCommand extends Command {
	public PauseCommand() {
		super("pause", Category.MUSIC, Permission.UNKNOWN, 0);
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
		if (!MusicUtils.canInteract(ctx.getMember(), playingTrack)) {
			ctx.replyErrorLocalized("music.messages.failure.cant_interact_requester", "pause the playback");
			return false;
		}
		musicPlayer.pauseOrUnpause();
		return true;
	}
}