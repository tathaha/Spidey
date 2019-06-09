package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.PermissionError;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@SuppressWarnings("unused")
public class SayCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final String neededPerm = "BAN_MEMBERS";

		if (e.getMember() != null && !Utils.hasPerm(e.getMember(), Permission.valueOf(neededPerm))) {

			Utils.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);

		}

		else {

			Utils.deleteMessage(e.getMessage());
			String toSay = e.getMessage().getContentRaw().substring(6);

			if (e.getMessage().getMentionedChannels().isEmpty()) {

				Utils.sendMessage(e.getChannel(), toSay, false);

			}

			else {

				final TextChannel ch = e.getMessage().getMentionedChannels().get(0);
				toSay = toSay.substring(0, toSay.lastIndexOf(' '));
				Utils.sendMessage(ch, toSay, false);

			}

		}

	}

	@Override
	public final String help() { return "Spidey will say something for you (in specified channel)"; }
	@Override
	public final boolean isAdmin() { return true; }
	@Override
	public final String invoke() { return "say"; }

}