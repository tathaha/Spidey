package dev.mlnr.spidey.commands.settings.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class DefaultVolumeCommand extends Command {
	public DefaultVolumeCommand() {
		super("defaultvolume", Category.Settings.MUSIC, Permission.UNKNOWN, 0,
				new OptionData(OptionType.INTEGER, "volume", "The default music volume for this server or blank to see the current default volume"));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		if (!MusicUtils.canInteract(ctx.getMember())) {
			ctx.replyErrorLocalized("music.messages.failure.cant_interact", "set the default music volume");
			return false;
		}
		var volumeOption = ctx.getLongOption("volume");
		var guildId = ctx.getGuild().getIdLong();
		var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
		var musicSettings = guildSettingsCache.getMusicSettings(guildId);
		var currentDefaultVolume = musicSettings.getDefaultVolume();
		if (volumeOption == null) {
			ctx.replyLocalized("commands.defaultvolume.other.current", currentDefaultVolume);
			return true;
		}
		var newDefaultVolume = (int) Math.min(volumeOption, 150);
		if (newDefaultVolume == currentDefaultVolume) {
			ctx.replyErrorLocalized("commands.defaultvolume.other.already_set", newDefaultVolume);
			return true;
		}
		musicSettings.setDefaultVolume(newDefaultVolume);
		ctx.replyLocalized("commands.defaultvolume.other.set", newDefaultVolume);
		return true;
	}
}