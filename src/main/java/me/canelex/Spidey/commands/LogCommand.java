package me.canelex.Spidey.commands;

import java.util.concurrent.TimeUnit;

import me.canelex.Spidey.MySQL;
import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.PermissionError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LogCommand implements ICommand {  	
	
	@Override
	public boolean called(GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {

		final String neededPerm = "ADMINISTRATOR";
		
		if (API.hasPerm(e.getMember(), Permission.valueOf(neededPerm))) {
			
			API.deleteMessage(e.getMessage());
			
			if (e.getGuild().getSystemChannel() != null) {
				
				e.getGuild().getManager().setSystemChannel(null).queue();    				
				
			}
			
			if (!MySQL.isInDatabase(e.getGuild().getIdLong())) {    			
    				    				
            	MySQL.insertData(e.getGuild().getIdLong(), e.getChannel().getIdLong());
            	e.getChannel().sendMessage(":white_check_mark: Log channel set to " + e.getChannel().getAsMention() + ". Type this command again to set log channel to default guild channel.").queue(m -> m.delete().queueAfter(5,  TimeUnit.SECONDS));
				
			}
			
			else {
				
    			if (MySQL.getChannelId(e.getGuild().getIdLong()).equals(e.getChannel().getIdLong())) {
    				
    				MySQL.removeData(e.getGuild().getIdLong());
    				MySQL.insertData(e.getGuild().getIdLong(), e.getGuild().getDefaultChannel().getIdLong());
    				e.getChannel().sendMessage(":white_check_mark: Log channel set to " + e.getGuild().getDefaultChannel().getAsMention() + ". Type this command again in channel you want to be as log channel.").queue(m -> m.delete().queueAfter(5,  TimeUnit.SECONDS));
    				
    			}
    			
    			else {
    				
    				MySQL.removeData(e.getGuild().getIdLong());    				
            		MySQL.insertData(e.getGuild().getIdLong(), e.getChannel().getIdLong());
            		e.getChannel().sendMessage(":white_check_mark: Log channel set to " + e.getChannel().getAsMention() + ". Type this command again to set log channel to default guild channel.").queue(m -> m.delete().queueAfter(5,  TimeUnit.SECONDS));  				
    				
    			}        			
				
			}

		}
		
		else {
			
    		API.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);    			
			
		}
		
	}

	@Override
	public String help() {

		return "Sets log channel";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {

		return;
		
	}	

}