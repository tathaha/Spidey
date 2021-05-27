package dev.mlnr.spidey.commands.settings.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class SegmentSkippingCommand extends Command {

	public SegmentSkippingCommand() {
		super("segmentskipping", new String[]{"segmentskip", "segskip", "skipping", "segskipping"}, Category.Settings.MUSIC, Permission.UNKNOWN, 0, 4);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var i18n = ctx.getI18n();
		if (!MusicUtils.canInteract(ctx.getMember())) {
			ctx.replyErrorLocalized("music.messages.failure.cant_interact", "enable/disable segment skipping");
			return false;
		}
		var musicSettings = ctx.getCache().getGuildSettingsCache().getMusicSettings(ctx.getGuild().getIdLong());
		var enabled = !musicSettings.isSegmentSkippingEnabled();
		musicSettings.setSegmentSkippingEnabled(enabled);
		ctx.reactLike();
		ctx.reply(i18n.get("commands.segmentskipping.other.done.text", enabled ? i18n.get("enabled") : i18n.get("disabled")) +
				(enabled ? " " + i18n.get("commands.segmentskipping.other.done.warning") : ""));
		return true;
	}
}