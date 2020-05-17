package me.canelex.spidey.commands.moderation;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Message;
import me.canelex.jda.api.entities.TextChannel;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;

@SuppressWarnings("unused")
public class SlowmodeCommand extends Command
{
	private static final int maxSlowmode = TextChannel.MAX_SLOWMODE;
	private static final int hours = maxSlowmode / 3600;
	private static final String desc = "Sets the slowmode of the channel. Limit: `" + maxSlowmode + "s` - `" + hours + "h`. Example - `slowmode <seconds | off>`";

	public SlowmodeCommand()
	{
		super("slowmode", new String[]{}, desc, "slowmode <seconds/off>", Category.MODERATION, Permission.MANAGE_CHANNEL, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var channel = message.getChannel();
		final var requiredPermission = getRequiredPermission();
		if (!Utils.hasPerm(message.getMember(), requiredPermission))
			Utils.getPermissionsError(requiredPermission, message);
		else
		{
			var seconds = 0;
			final var par = message.getContentRaw().substring(11);
			if (!(par.equalsIgnoreCase("off") || par.equalsIgnoreCase("false")))
			{
				try
				{
					seconds = Math.max(0, Math.min(Integer.parseInt(par), TextChannel.MAX_SLOWMODE));
				}
				catch (final NumberFormatException ignored)
				{
					Utils.sendMessage(channel, ":no_entry: Couldn't parse argument.");
					return;
				}
			}
			message.getTextChannel().getManager().setSlowmode(seconds).queue();
		}
	}
}