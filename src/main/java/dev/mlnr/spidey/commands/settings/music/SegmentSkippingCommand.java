package dev.mlnr.spidey.commands.settings.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@SuppressWarnings("unused")
public class SegmentSkippingCommand extends Command {
	public SegmentSkippingCommand() {
		super("segmentskipping", "Enables/disables non-music segment skipping using SponsorBlock", Category.Settings.MUSIC, Permission.UNKNOWN, 4,
				Utils.createConvenientOption(OptionType.BOOLEAN, "enable", "Whether to enable support for SponsorBlock segments", true));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		if (!MusicUtils.canInteract(ctx.getMember())) {
			ctx.replyErrorLocalized("music.messages.failure.cant_interact", "enable/disable segment skipping");
			return false;
		}
		var musicSettings = ctx.getCache().getGuildSettingsCache().getMusicSettings(ctx.getGuild().getIdLong());
		var enabled = ctx.getBooleanOption("enable");
		var i18n = ctx.getI18n();
		musicSettings.setSegmentSkippingEnabled(enabled);
		ctx.reply(i18n.get("commands.segmentskipping.done.text", enabled ? i18n.get("enabled") : i18n.get("disabled")) +
				(enabled ? " " + i18n.get("commands.segmentskipping.done.warning") : ""));
		return true;
	}
}