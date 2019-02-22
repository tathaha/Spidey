package me.canelex.Spidey.commands;

import me.canelex.Spidey.objects.command.Command;
import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.PermissionError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SayCommand implements Command {

	@Override
	public boolean called(GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {
		
		final String neededPerm = "BAN_MEMBERS";
		
		if (!API.hasPerm(e.getMember(), Permission.valueOf(neededPerm))) {
			
			API.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);
			
		}
		
		else {
			
			API.deleteMessage(e.getMessage());
    		String toSay = e.getMessage().getContentRaw().substring(6);
    		
    		if (e.getMessage().getMentionedChannels().isEmpty()) {
    			
    			API.sendMessage(e.getChannel(), toSay, false);
    			
    		}
    		
    		else {
    			
    			TextChannel ch = e.getMessage().getMentionedChannels().get(0);
    			toSay = toSay.substring(0, toSay.lastIndexOf(" "));
    			API.sendMessage(ch, toSay, false);
    			
    		}    			
			
		}
		
	}

	@Override
	public String help() {

		return "Spidey will say something for you (in specified channel)";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {

		return;
		
	}

}