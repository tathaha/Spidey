package dev.mlnr.spidey.commands.slash.settings.music;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class DefaultVolumeSlashCommand extends SlashCommand {
	public DefaultVolumeSlashCommand() {
		super("defaultvolume", "Sets the default music volume", Category.Settings.MUSIC, Permission.UNKNOWN, 0,
				new OptionData(OptionType.INTEGER, "volume", "The default music volume for this server or blank to see the current default volume")
						.setRequiredRange(0, 150));
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
		if (!MusicUtils.canInteract(ctx.getMember())) {
			ctx.replyErrorLocalized("music.messages.failure.cant_interact", "set the default music volume");
			return false;
		}
		var newDefaultVolume = ctx.getLongOption("volume");
		var guildId = ctx.getGuild().getIdLong();
		var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
		var musicSettings = guildSettingsCache.getMusicSettings(guildId);
		var currentDefaultVolume = musicSettings.getDefaultVolume();
		if (newDefaultVolume == null) {
			ctx.replyLocalized("commands.defaultvolume.current", currentDefaultVolume);
			return true;
		}
		if (newDefaultVolume == currentDefaultVolume) {
			ctx.replyErrorLocalized("commands.defaultvolume.already_set", newDefaultVolume);
			return true;
		}
		musicSettings.setDefaultVolume(Math.toIntExact(newDefaultVolume));
		ctx.replyLocalized("commands.defaultvolume.set", newDefaultVolume);
		return true;
	}
}