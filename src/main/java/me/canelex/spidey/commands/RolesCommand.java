package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RolesCommand implements ICommand {

	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {
				
		if (e.getMessage().getMentionedMembers().isEmpty()) {
			
			final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
			eb.setColor(Color.ORANGE);
			
			final List<Role> roles = e.getGuild().getRoleCache().stream().collect(Collectors.toCollection(ArrayList::new));
	        roles.remove(e.getGuild().getPublicRole());			
			
	        StringBuilder s = new StringBuilder();
	        
	        int i = 0;
	        
	        for (final Role role : roles) {
	        	
	        	i++;
	        	
	        	if (i == roles.size()) {
	        		
	        		s.append(role.getName());
	        		
	        	}
	        	
	        	else {
	        		
	        		s.append(role.getName() + ", ");
	        		
	        	}
	        	
	        }
	        
	        eb.setDescription("Roles of **" + e.getGuild().getName() + "**\n\n" + ((i == 0) ? "None" : s + " (**" + i + "**)"));
	        
	        API.sendMessage(e.getChannel(), eb.build());
			
		}
		
		else {
			
			final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
			eb.setColor(Color.ORANGE);
			
	        StringBuilder s = new StringBuilder();
	        
	        int i = 0;
	        
	        for (final Role role : e.getMessage().getMentionedMembers().get(0).getRoles()) {
	        	
	        	i++;
	        	
	        	if (i == e.getMessage().getMentionedMembers().get(0).getRoles().size()) {
	        		
	        		s.append(role.getName());
	        		
	        	}
	        	
	        	else {
	        		
	        		s.append(role.getName() + ", ");
	        		
	        	}
	        	
	        }
	        
	        eb.setDescription("Roles of **" + e.getMessage().getMentionedMembers().get(0).getUser().getAsTag() + "**\n\n" + ((i == 0) ? "None" : s.toString() + " (**" + i + "**)"));
	        
	        API.sendMessage(e.getChannel(), eb.build());			
			
		}
		
	}

	@Override
	public final String help() {

		return "Returns roles of guild if nobody is mentioned";
		
	}		

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}

}