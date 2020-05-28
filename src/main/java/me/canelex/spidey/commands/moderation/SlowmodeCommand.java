package me.canelex.spidey.commands.moderation;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

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
		final var textChannel = message.getTextChannel();
		final var manager = textChannel.getManager();

		if (!Utils.hasPerm(message.getMember(), requiredPermission))
		{
			Utils.getPermissionsError(requiredPermission, message);
			return;
		}
		if (args.length == 1)
		{
			if (textChannel.getSlowmode() == 0)
				Utils.returnError("There is no slowmode in this channel", message);
			else
			{
				manager.setSlowmode(0).queue();
				Utils.sendMessage(channel, ":white_check_mark: Slowmode for this channel has been disabled.");
			}
			return;
		}

		var seconds = 0;
		if (!args[1].equalsIgnoreCase("off"))
		{
			try
			{
				seconds = Math.max(0, Math.min(Integer.parseInt(args[1]), TextChannel.MAX_SLOWMODE));
			}
			catch (final NumberFormatException ignored)
			{
				Utils.sendMessage(channel, ":no_entry: Couldn't parse argument.");
				return;
			}
		}
		manager.setSlowmode(seconds).queue();
	}
}