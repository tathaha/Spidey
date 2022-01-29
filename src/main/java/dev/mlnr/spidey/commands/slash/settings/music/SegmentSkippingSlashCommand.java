package dev.mlnr.spidey.commands.slash.settings.music;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class SegmentSkippingSlashCommand extends SlashCommand {
	public SegmentSkippingSlashCommand() {
		super("segmentskipping", "Enables/disables non-music segment skipping using SponsorBlock", Category.Settings.MUSIC, Permission.UNKNOWN, 4,
				new OptionData(OptionType.BOOLEAN, "enable", "Whether to enable support for SponsorBlock segments", true));
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
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