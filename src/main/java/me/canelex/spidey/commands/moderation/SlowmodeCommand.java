package me.canelex.spidey.commands.moderation;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import static net.dv8tion.jda.api.entities.TextChannel.MAX_SLOWMODE;

@SuppressWarnings("unused")
public class SlowmodeCommand extends Command
{
	private static final int HOURS = MAX_SLOWMODE / 3600;
	private static final String DESC = "Sets the slowmode of the channel. Limit: `" + MAX_SLOWMODE + "s` - `" + HOURS + "h`. Example - `slowmode <seconds | off>`";

	public SlowmodeCommand()
	{
		super("slowmode", new String[]{}, DESC, "slowmode <seconds/off>", Category.MODERATION, Permission.MANAGE_CHANNEL, 0);
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
			checkSlowmode(textChannel, message);
			return;
		}
		if (args[1].equalsIgnoreCase("off"))
		{
			checkSlowmode(textChannel, message);
			return;
		}

		try
		{
			var parsed = Math.max(0, Math.min(Integer.parseInt(args[1]), MAX_SLOWMODE));
			if (parsed == 0)
			{
				checkSlowmode(textChannel, message);
				return;
			}
			else if (textChannel.getSlowmode() == parsed)
			{
				Utils.returnError("The slowmode for this channel is already set to **" + parsed + "** seconds", message);
				return;
			}
			manager.setSlowmode(parsed).queue();
			Utils.sendMessage(channel, ":white_check_mark: The slowmode for this channel has been set to **" + parsed + "** seconds!");
		}
		catch (final NumberFormatException ex)
		{
			Utils.returnError("Couldn't parse argument", message);
		}
	}

	private void checkSlowmode(final TextChannel channel, final Message message)
	{
		if (channel.getSlowmode() == 0)
			Utils.returnError("There is no slowmode in this channel", message);
		else
		{
			channel.getManager().setSlowmode(0).queue();
			Utils.sendMessage(channel, ":white_check_mark: The slowmode for this channel has been disabled!");
		}
	}
}