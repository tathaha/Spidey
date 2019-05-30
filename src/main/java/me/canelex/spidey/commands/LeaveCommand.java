package me.canelex.spidey.commands;

import me.canelex.spidey.MySQL;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LeaveCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		if (e.getMember() != e.getGuild().getOwner()) {

			API.sendMessage(e.getChannel(), e.getAuthor().getAsMention() + ", you have to be the guild owner to do this.", false);

		}

		else {

			e.getChannel().sendMessage("Bye.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
			API.deleteMessage(e.getMessage());
			API.sendPrivateMessageFormat(Objects.requireNonNull(e.getGuild().getOwner()).getUser(), "I've left your server **%s**. If you'd want to invite me back, please use this URL: ||%s||. Thanks for using **Spidey**!", false, e.getGuild().getName(), API.getInviteUrl(e.getGuild().getIdLong()));
			MySQL.removeData(e.getGuild().getIdLong());
			e.getGuild().leave().queue();

		}

	}

	@Override
	public final String help() {

		return "Spidey will leave your server";

	}

	@Override
	public final boolean isAdmin() {
		return true;
	}

}