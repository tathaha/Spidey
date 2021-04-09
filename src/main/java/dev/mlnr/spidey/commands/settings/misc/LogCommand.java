package dev.mlnr.spidey.commands.settings.misc;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

@SuppressWarnings("unused")
public class LogCommand extends Command {

	public LogCommand() {
		super("log", new String[]{}, Category.Settings.MISC, Permission.MANAGE_SERVER, 1, 4);
	}

	@Override
	public boolean execute(String[] args, CommandContext ctx) {
		var guildId = ctx.getGuild().getIdLong();
		if (args.length == 0) {
			proceed(guildId, ctx.getTextChannel(), ctx);
			return true;
		}
		ctx.getArgumentAsTextChannel(0, channel -> proceed(guildId, channel, ctx));
		return true;
	}

	private void proceed(long guildId, TextChannel channel, CommandContext ctx) {
		var miscSettings = ctx.getCache().getGuildSettingsCache().getMiscSettings(guildId);
		var channelId = channel.getIdLong();
		if (miscSettings.getLogChannelId() == channelId) {
			miscSettings.removeLogChannel();
			ctx.replyLocalized("commands.log.other.reset");
			return;
		}
		if (!channel.canTalk()) {
			var message = ctx.getMessage();
			Utils.addReaction(message, Emojis.CROSS);
			Utils.addReaction(message, "\uD83D\uDE4A");
			return;
		}
		miscSettings.setLogChannelId(channelId);
		ctx.replyLocalized("commands.log.other.set", channelId);
	}
}