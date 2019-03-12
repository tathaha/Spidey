package me.canelex.Spidey.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class RolesCommand implements ICommand {

	@Override
	public boolean called(GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {
				
		if (e.getMessage().getMentionedMembers().isEmpty()) {
			
			EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
			eb.setColor(Color.ORANGE);
			
	        List<Role> roles = e.getGuild().getRoles().stream().collect(Collectors.toCollection(ArrayList::new));
	        roles.remove(e.getGuild().getPublicRole());			
			
	        String s = "";
	        
	        int i = 0;
	        
	        for (Role role : roles) {
	        	
	        	i++;
	        	
	        	if (i == roles.size()) {
	        		
	        		s += role.getName();
	        		
	        	}
	        	
	        	else {
	        		
	        		s += role.getName() + ", ";
	        		
	        	}
	        	
	        }
	        
	        eb.setDescription("Roles of **" + e.getGuild().getName() + "**\n\n" + ((i == 0) ? "None" : s + " (**" + i + "**)"));
	        
	        API.sendMessage(e.getChannel(), eb.build());
			
		}
		
		else {
			
			EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
			eb.setColor(Color.ORANGE);
			
	        String s = "";
	        
	        int i = 0;
	        
	        for (Role role : e.getMessage().getMentionedMembers().get(0).getRoles()) {
	        	
	        	i++;
	        	
	        	if (i == e.getMessage().getMentionedMembers().get(0).getRoles().size()) {
	        		
	        		s += role.getName();
	        		
	        	}
	        	
	        	else {
	        		
	        		s += role.getName() + ", ";
	        		
	        	}
	        	
	        }
	        
	        eb.setDescription("Roles of **" + e.getMessage().getMentionedMembers().get(0).getUser().getAsTag() + "**\n\n" + ((i == 0) ? "None" : s + " (**" + i + "**)"));
	        
	        API.sendMessage(e.getChannel(), eb.build());			
			
		}
		
	}

	@Override
	public String help() {

		return "Returns roles of guild if nobody is mentioned";
		
	}		

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		
		return;
		
	}

}