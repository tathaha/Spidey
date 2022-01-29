package dev.mlnr.spidey.commands.slash.music;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class PauseSlashCommand extends SlashCommand {
	public PauseSlashCommand() {
		super("pause", "Pauses/unpauses the playback", Category.MUSIC, Permission.UNKNOWN, 0);
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
		if (!MusicUtils.canInteract(ctx.getMember(), playingTrack)) {
			ctx.replyErrorLocalized("music.messages.failure.cant_interact_requester", "pause the playback");
			return false;
		}
		var paused = musicPlayer.pauseOrUnpause();
		var i18n = ctx.getI18n();
		ctx.replyLocalized("commands.pause.state", paused ? i18n.get("commands.pause.paused") : i18n.get("commands.pause.unpaused"));
		return true;
	}
}