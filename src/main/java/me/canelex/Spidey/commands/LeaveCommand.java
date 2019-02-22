package me.canelex.Spidey.commands;

import me.canelex.Spidey.MySQL;
import me.canelex.Spidey.objects.command.Command;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LeaveCommand implements Command {

	@Override
	public boolean called(GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {

		if (e.getMember() != e.getGuild().getOwner()) {
			
			API.sendMessage(e.getChannel(), e.getAuthor().getAsMention() + ", you have to be the guild owner to do this.", false);
			
		}
		
		else {
			
    		API.sendMessage(e.getChannel(), "Bye.", false);
    		API.sendPrivateMessage(e.getGuild().getOwner().getUser(), String.format("I've left your server **%s**. If you'll want to invite me back, please use this URL: ||%s||. Thanks for using **Spidey**!", e.getGuild().getName(), API.getInviteUrl(e.getGuild().getIdLong())), false);
    		MySQL.removeData(e.getGuild().getIdLong());
    		e.getGuild().leave().queue();    			
			
		}		
		
	}

	@Override
	public String help() {

		return "Spidey will leave your server";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		
		return;
		
	}

}