package me.canelex.Spidey.commands;

import java.awt.Color;

import me.canelex.Spidey.MySQL;
import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.PermissionError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class WarnCommand implements ICommand {

	@Override
	public boolean called(GuildMessageReceivedEvent e) {
		
		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {
		
		final String neededPerm = "BAN_MEMBERS";    		
		
		if (!e.getMessage().getContentRaw().equals("s!warn")) {
			
			if (API.hasPerm(e.getMember(), Permission.valueOf(neededPerm))) {
				
				if (e.getGuild().getTextChannelById(MySQL.getChannelId(e.getGuild().getIdLong())) == null) {
					
					return;
					
				}
				
				else {
					
					  TextChannel log = e.getGuild().getTextChannelById(MySQL.getChannelId(e.getGuild().getIdLong()));
					  final String reason;
				      reason = e.getMessage().getContentRaw().substring(7, e.getMessage().getContentRaw().lastIndexOf(" "));
				
				      for (User u : e.getMessage().getMentionedUsers()) {
				
				        API.sendPrivateMessageFormat(u, ":exclamation: You have been warned on guild **%s** from **%s** for **%s**.", false, e.getGuild().getName(), e.getAuthor().getName(), e.getAuthor().getName());
				
				        API.deleteMessage(e.getMessage());
				        EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
				        eb.setTitle("NEW WARN");
				        eb.setColor(Color.ORANGE);
				        eb.addField("User", u.getAsMention(), true);
				        eb.addField("Moderator", e.getAuthor().getAsMention(), true);
				        eb.addField("Reason", "**" + reason + "**", true);
				        API.sendMessage(log, eb.build());     					  
					      					
	                    }    				    			 			
				
			    }
				
			}
			
			else {
				
				API.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);    				
				
			}
			
		}		
		
	}

	@Override
	public String help() {

		return "Warns user";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		
		return;
		
	}

}