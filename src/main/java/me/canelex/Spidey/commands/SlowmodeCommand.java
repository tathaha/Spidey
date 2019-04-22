package me.canelex.Spidey.commands;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.PermissionError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SlowmodeCommand implements ICommand {

	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {

		return true;

	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final String neededPerm = "ADMINISTRATOR";

		if (!API.hasPerm(e.getMember(), Permission.valueOf(neededPerm))) {

			API.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);

		}

		else {

			int seconds;

			final String par = e.getMessage().getContentRaw().substring(11);

			if (par.equals("off") || par.equals("false")) {

				seconds = 0;

			}

			else {

				try {

					seconds = Math.max(0, Math.min(Integer.parseInt(par), 21600));

				}

				catch (final NumberFormatException ignored) {

					API.sendMessage(e.getChannel(), ":no_entry: Couldn't parse argument.", false);
					return;

				}

			}

			e.getChannel().getManager().setSlowmode(seconds).queue();

		}

	}

	@Override
	public final String help() {

		return "Sets a slowmode for channel. Limit: `21600s` - `6h`. Example - `s!slowmode <seconds | off>`";

	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}

}