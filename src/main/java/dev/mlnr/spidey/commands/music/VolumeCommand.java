package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.CommandBase;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class VolumeCommand extends CommandBase {
	public VolumeCommand() {
		super("volume", "Sets the volume of music", Category.MUSIC, Permission.UNKNOWN, 0,
				new OptionData(OptionType.INTEGER, "volume", "The new volume", true));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var i18n = ctx.getI18n();
		var cache = ctx.getCache();
		var guild = ctx.getGuild();
		var musicPlayer = cache.getMusicPlayerCache().getMusicPlayer(guild);
		if (musicPlayer == null) {
			ctx.replyErrorLocalized("music.messages.failure.no_music");
			return false;
		}
		var playingTrack = musicPlayer.getPlayingTrack();
		var member = ctx.getMember();
		if (playingTrack == null ? !MusicUtils.canInteract(member) : !MusicUtils.canInteract(member, playingTrack)) {
			var error = playingTrack == null
					? i18n.get("music.messages.failure.cant_interact", "change the volume")
					: i18n.get("music.messages.failure.cant_interact_requester", "change the volume");
			ctx.replyError(error);
			return false;
		}
		var currentVolume = musicPlayer.getVolume();
		var newVolume = (int) Math.min(ctx.getLongOption("volume"), 150);
		if (!checkNewVolume(ctx, newVolume, currentVolume)) {
			return false;
		}
		musicPlayer.setVolume(newVolume);
		ctx.replyLocalized("commands.volume.set", newVolume);
		return true;
	}

	private boolean checkNewVolume(CommandContext ctx, int newVolume, int currentVolume) {
		if (newVolume == currentVolume) {
			ctx.replyErrorLocalized("commands.volume.already_set", newVolume);
			return false;
		}
		return true;
	}
}