package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class DefaultVolumeCommand extends Command {

	public DefaultVolumeCommand() {
		super("defaultvolume", new String[]{"defaultvol", "defvol"}, Category.SETTINGS, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var i18n = ctx.getI18n();
		if (!MusicUtils.canInteract(ctx.getMember())) {
			ctx.replyError(i18n.get("music.messages.failure.cant_interact", "set the default music volume"));
			return;
		}
		var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
		var guildId = ctx.getGuild().getIdLong();
		var currentDefaultVolume = guildSettingsCache.getDefaultVolume(guildId);
		if (args.length == 0) {
			ctx.reply(i18n.get("commands.defaultvolume.other.current", currentDefaultVolume, guildSettingsCache.getPrefix(guildId)));
			return;
		}
		ctx.getArgumentAsUnsignedInt(0, parsedVolume -> {
			var newDefaultVolume = Math.min(parsedVolume, 150);
			if (newDefaultVolume == currentDefaultVolume) {
				ctx.replyError(i18n.get("commands.defaultvolume.other.already_set", newDefaultVolume));
				return;
			}
			guildSettingsCache.setDefaultVolume(guildId, newDefaultVolume);
			ctx.reactLike();
			ctx.reply(i18n.get("commands.defaultvolume.other.set", newDefaultVolume));
		});
	}
}