package me.canelex.Spidey.commands;

import me.canelex.Spidey.MySQL;
import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LeaveCommand implements ICommand {

	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		if (e.getMember() != e.getGuild().getOwner()) {
			
			API.sendMessage(e.getChannel(), e.getAuthor().getAsMention() + ", you have to be the guild owner to do this.", false);
			
		}
		
		else {
			
    		API.sendMessage(e.getChannel(), "Bye.", false);
    		API.sendPrivateMessageFormat(e.getGuild().getOwner().getUser(), "I've left your server **%s**. If you'll want to invite me back, please use this URL: ||%s||. Thanks for using **Spidey**!", false, e.getGuild().getName(), API.getInviteUrl(e.getGuild().getIdLong()));
    		MySQL.removeData(e.getGuild().getIdLong());
    		e.getGuild().leave().queue();    			
			
		}		
		
	}

	@Override
	public final String help() {

		return "Spidey will leave your server";
		
	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}

}