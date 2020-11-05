package dev.mlnr.spidey.commands.moderation;

import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class LogCommand extends Command
{
	public LogCommand()
	{
		super ("log", new String[]{}, "Sets log channel", "log", Category.MODERATION, Permission.ADMINISTRATOR, 0, 2);
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
		GuildSettingsCache.setLogChannelId(guildId, channelId);
		ctx.reply(":white_check_mark: The log channel has been set to <#" + channelId + ">!");
	}
}