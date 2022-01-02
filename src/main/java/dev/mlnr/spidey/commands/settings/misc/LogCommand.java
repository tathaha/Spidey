package dev.mlnr.spidey.commands.settings.misc;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class LogCommand extends Command {
	public LogCommand() {
		super("log", "Sets the log channel", Category.Settings.MISC, Permission.MANAGE_SERVER, 4,
				new OptionData(OptionType.CHANNEL, "channel", "The channel to set as the log channel")
						.setChannelTypes(ChannelType.TEXT));
		withFlags(Command.Flags.NO_THREADS);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var channelOption = ctx.getChannelOption("channel");
		var channel = channelOption == null ? ctx.getTextChannel() : channelOption;
		var guildId = ctx.getGuild().getIdLong();
		var miscSettings = ctx.getCache().getGuildSettingsCache().getMiscSettings(guildId);
		var channelId = channel.getIdLong();
		if (miscSettings.getLogChannelId() == channelId) {
			miscSettings.removeLogChannel();
			ctx.replyLocalized("commands.log.reset");
			return true;
		}
		miscSettings.setLogChannelId(channelId);
		ctx.replyLocalized("commands.log.set", channelId);
		return true;
	}
}