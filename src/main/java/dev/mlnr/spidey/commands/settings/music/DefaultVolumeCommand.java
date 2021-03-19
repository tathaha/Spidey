package dev.mlnr.spidey.commands.settings.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class DefaultVolumeCommand extends Command {

	public DefaultVolumeCommand() {
		super("defaultvolume", new String[]{"defaultvol", "defvol"}, Category.Settings.MUSIC, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		if (!MusicUtils.canInteract(ctx.getMember())) {
			ctx.replyErrorLocalized("music.messages.failure.cant_interact", "set the default music volume");
			return;
		}
		var guildId = ctx.getGuild().getIdLong();
		var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
		var musicSettings = guildSettingsCache.getMusicSettings(guildId);
		var currentDefaultVolume = musicSettings.getDefaultVolume();
		if (args.length == 0) {
			var prefix = guildSettingsCache.getMiscSettings(guildId).getPrefix();
			ctx.replyLocalized("commands.defaultvolume.other.current", currentDefaultVolume, prefix);
			return;
		}
		ctx.getArgumentAsUnsignedInt(0, parsedVolume -> {
			var newDefaultVolume = Math.min(parsedVolume, 150);
			if (newDefaultVolume == currentDefaultVolume) {
				ctx.replyErrorLocalized("commands.defaultvolume.other.already_set", newDefaultVolume);
				return;
			}
			musicSettings.setDefaultVolume(newDefaultVolume);
			ctx.reactLike();
			ctx.replyLocalized("commands.defaultvolume.other.set", newDefaultVolume);
		});
	}
}