package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.MusicUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class PauseCommand extends Command {

	public PauseCommand() {
		super("pause", new String[]{"unpause"}, Category.MUSIC, Permission.UNKNOWN, 0, 0);
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
		if (!MusicUtils.canInteract(ctx.getMember(), playingTrack)) {
			ctx.replyError(i18n.get("music.messages.failure.cant_interact_requester", "pause the playback"));
			return;
		}
		var paused = musicPlayer.pauseOrUnpause();
		Utils.addReaction(ctx.getMessage(), paused ? "\u23F8\uFE0F" : Emojis.FORWARD);
	}
}