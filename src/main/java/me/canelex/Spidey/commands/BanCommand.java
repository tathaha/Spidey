package me.canelex.Spidey.commands;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.PermissionError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BanCommand implements ICommand {

	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {
		
		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {
		
		final String neededPerm = "BAN_MEMBERS";    		
		
		if (!e.getMessage().getContentRaw().equals("s!ban")) { //TODO rewrite
			
			if (e.getMember().hasPermission(Permission.valueOf(neededPerm))) {
				
        		String id = e.getMessage().getContentRaw().substring(6);
        		id = id.substring(0, id.indexOf(" "));
        		
        		final String reason = e.getMessage().getContentRaw().substring((7 + id.length()));
        		
				e.getGuild().getController().ban(id, 0, reason).queue();   				    				
				
			}
			
			else {
				
				API.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);    				
				
			}    			
			
		}		
		
	}

	@Override
	public final String help() {
		
		return "Bans user";
		
	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {
		
		return;
		
	}

}