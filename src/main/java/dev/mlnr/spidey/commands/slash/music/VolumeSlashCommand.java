package dev.mlnr.spidey.commands.slash.music;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class VolumeSlashCommand extends SlashCommand {
	public VolumeSlashCommand() {
		super("volume", "Sets the volume of music", Category.MUSIC, Permission.UNKNOWN, 0,
				new OptionData(OptionType.INTEGER, "volume", "The new volume", true)
						.setRequiredRange(0, 150));
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
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
		var newVolume = ctx.getLongOption("volume");
		if (newVolume == currentVolume) {
			ctx.replyErrorLocalized("commands.volume.already_set", currentVolume);
			return false;
		}
		musicPlayer.setVolume(Math.toIntExact(newVolume));
		ctx.replyLocalized("commands.volume.set", newVolume);
		return true;
	}
}