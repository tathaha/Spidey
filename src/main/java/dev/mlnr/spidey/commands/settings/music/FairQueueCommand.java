package dev.mlnr.spidey.commands.settings.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.settings.guild.GuildMusicSettings;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class FairQueueCommand extends Command {

	public FairQueueCommand() {
		super("fairqueue", new String[]{"fq"}, Category.Settings.MUSIC, Permission.UNKNOWN, 0, 4);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		if (!MusicUtils.canInteract(ctx.getMember())) {
			ctx.replyErrorLocalized("music.messages.failure.cant_interact", "enable/disable the fair queue or to set the threshold");
			return;
		}
		var musicSettings = ctx.getCache().getGuildSettingsCache().getMusicSettings(ctx.getGuild().getIdLong());
		if (args.length == 0) {
			manageFairQueue(musicSettings, ctx, !musicSettings.isFairQueueEnabled());
			return;
		}
		ctx.getArgumentAsUnsignedInt(0, threshold -> {
			if (threshold == 0) {
				manageFairQueue(musicSettings, ctx, false);
				return;
			}
			if (threshold < 2 || threshold > 10) {
				ctx.replyErrorLocalized("commands.fairqueue.other.threshold_number");
				return;
			}
			if (threshold == musicSettings.getFairQueueThreshold()) {
				ctx.replyErrorLocalized("commands.fairqueue.other.already_set", threshold);
				return;
			}
			manageFairQueue(musicSettings, ctx, true, threshold);
		});
	}

	private void manageFairQueue(GuildMusicSettings musicSettings, CommandContext ctx, boolean enabled) {
		manageFairQueue(musicSettings, ctx, enabled, -1);
	}

	private void manageFairQueue(GuildMusicSettings musicSettings, CommandContext ctx, boolean enabled, int threshold) {
		var i18n = ctx.getI18n();
		if (!enabled && !musicSettings.isFairQueueEnabled()) {
			ctx.replyErrorLocalized("commands.fairqueue.other.already_disabled");
			return;
		}
		if (threshold != -1) {
			musicSettings.setFairQueueThreshold(threshold);
		}
		musicSettings.setFairQueueEnabled(enabled);
		ctx.reactLike();
		ctx.reply(i18n.get("commands.fairqueue.other.done.text", enabled ? i18n.get("enabled") : i18n.get("disabled")) +
				(threshold == -1 ? "." : " " + i18n.get("commands.fairqueue.other.done.threshold", threshold)));
	}
}