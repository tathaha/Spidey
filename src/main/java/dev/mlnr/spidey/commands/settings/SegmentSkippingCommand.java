package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class SegmentSkippingCommand extends Command {

	public SegmentSkippingCommand() {
		super("segmentskipping", new String[]{"segmentskip", "segskip", "skipping", "segskipping"}, Category.SETTINGS, Permission.UNKNOWN, 0, 4);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var i18n = ctx.getI18n();
		if (!MusicUtils.canInteract(ctx.getMember())) {
			ctx.replyError(i18n.get("music.messages.failure.cant_interact", "enable/disable segment skipping"));
			return;
		}
		var guildSettingsCache = ctx.getCache().getGuildSettingsCache();
		var guildId = ctx.getGuild().getIdLong();
		var enabled = !guildSettingsCache.isSegmentSkippingEnabled(guildId);
		guildSettingsCache.setSegmentSkippingEnabled(guildId, enabled);
		ctx.reactLike();
		ctx.reply(i18n.get("commands.segmentskipping.other.done.text", enabled ? i18n.get("enabled") : i18n.get("disabled")) +
				(enabled ? " " + i18n.get("commands.segmentskipping.other.done.warning") : ""));
	}
}