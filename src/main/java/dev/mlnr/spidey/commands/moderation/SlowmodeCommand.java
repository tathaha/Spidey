package dev.mlnr.spidey.commands.moderation;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
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
		super("slowmode", new String[]{}, DESC, "slowmode <seconds/off>", Category.MODERATION, Permission.MANAGE_CHANNEL, 0, 4);
	}

	@Override
	public void execute(final String[] args, final Message msg)
	{
		final var textChannel = msg.getTextChannel();
		final var manager = textChannel.getManager();

		if (args.length == 0)
		{
			checkSlowmode(textChannel, msg);
			return;
		}
		if (args[0].equalsIgnoreCase("off"))
		{
			checkSlowmode(textChannel, msg);
			return;
		}

		try
		{
			var parsed = Math.max(0, Math.min(Integer.parseInt(args[0]), MAX_SLOWMODE));
			if (parsed == 0)
			{
				checkSlowmode(textChannel, msg);
				return;
			}
			else if (textChannel.getSlowmode() == parsed)
			{
				Utils.returnError("The slowmode for this channel is already set to **" + parsed + "** seconds", msg);
				return;
			}
			manager.setSlowmode(parsed).queue();
			Utils.sendMessage(textChannel, ":white_check_mark: The slowmode for this channel has been set to **" + parsed + "** seconds!");
		}
		catch (final NumberFormatException ex)
		{
			Utils.returnError("Couldn't parse argument", msg);
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