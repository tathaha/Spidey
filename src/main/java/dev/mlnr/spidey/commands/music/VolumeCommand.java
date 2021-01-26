package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class VolumeCommand extends Command {

	public VolumeCommand() {
		super("volume", new String[]{"vol"}, Category.MUSIC, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var i18n = ctx.getI18n();
		var cache = ctx.getCache();
		var guild = ctx.getGuild();
		var musicPlayer = cache.getMusicPlayerCache().getMusicPlayer(guild);
		if (musicPlayer == null) {
			ctx.replyError(i18n.get("music.messages.failure.no_music"));
			return;
		}
		var playingTrack = musicPlayer.getPlayingTrack();
		var member = ctx.getMember();
		if (playingTrack == null ? !MusicUtils.canInteract(member) : !MusicUtils.canInteract(member, playingTrack)) {
			var error = playingTrack == null
					? i18n.get("music.messages.failure.cant_interact", "change the volume")
					: i18n.get("music.messages.failure.cant_interact_requester", "change the volume");
			ctx.replyError(error);
			return;
		}
		var currentVolume = musicPlayer.getVolume();
		if (args.length == 0) {
			ctx.reply(i18n.get("commands.volume.other.current", currentVolume, cache.getGuildSettingsCache().getPrefix(guild.getIdLong())));
			return;
		}
		ctx.getArgumentAsInt(0, parsedVolume -> {
			if (args[0].charAt(0) != '+' && args[0].charAt(0) != '-') {
				var newVolume = Math.min(parsedVolume, 150);
				if (!checkNewVolume(ctx, newVolume, currentVolume)) {
					return;
				}
				musicPlayer.setVolume(newVolume);
				ctx.reactLike();
				ctx.reply(i18n.get("commands.volume.other.set", newVolume));
				return;
			}
			var newVolume = Math.min(Math.max(currentVolume + parsedVolume, 0), 150);
			if (!checkNewVolume(ctx, newVolume, currentVolume)) {
				return;
			}
			musicPlayer.setVolume(newVolume);
			ctx.reactLike();
			ctx.reply(i18n.get("commands.volume.other.set", newVolume));
		});
	}

	private boolean checkNewVolume(CommandContext ctx, int newVolume, int currentVolume) {
		if (newVolume == currentVolume) {
			ctx.replyError(ctx.getI18n().get("commands.volume.other.already_set", newVolume));
			return false;
		}
		return true;
	}
}