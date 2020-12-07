package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.cache.settings.GuildSettingsCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class LogCommand extends Command
{
	public LogCommand()
	{
		super ("log", new String[]{}, "Sets log channel", "log", Category.SETTINGS, Permission.MANAGE_SERVER, 0, 4);
	}

	@Override
	public void execute(final String[] args, final CommandContext ctx)
	{
		final var guild = ctx.getGuild();
		final var guildId = guild.getIdLong();
		final var channel = ctx.getTextChannel();

		final var channelId = channel.getIdLong();
		if (GuildSettingsCache.getLogChannelId(guildId) == channelId)
		{
			GuildSettingsCache.removeLogChannel(guildId);
			ctx.reply(":white_check_mark: The log channel has been reset!");
			return;
		}
		if (!channel.canTalk())
		{
			final var message = ctx.getMessage();
			Utils.addReaction(message, Emojis.CROSS);
			Utils.addReaction(message, "\uD83D\uDE4A");
			return;
		}
		GuildSettingsCache.setLogChannelId(guildId, channelId);
		ctx.reply(":white_check_mark: The log channel has been set to <#" + channelId + ">!");
	}
}