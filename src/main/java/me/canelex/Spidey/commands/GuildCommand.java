package me.canelex.Spidey.commands;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

import me.canelex.Spidey.objects.command.Command;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GuildCommand implements Command {
	
	Locale locale = new Locale("en", "EN");  
	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));        	
	SimpleDateFormat date = new SimpleDateFormat("EEEE, d.LLLL Y", locale);      
	SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", locale);  	
	
	@Override
	public boolean called(GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {

    	EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());         	
    	eb.setTitle(e.getGuild().getName());
    	eb.setColor(Color.ORANGE);
    	eb.setThumbnail(e.getGuild().getIconUrl());
    	eb.setDescription("Server ID: **" + e.getGuild().getId() + "**");
    	    		
		eb.addField("Owner", "**" + e.getGuild().getOwner().getAsMention() + "**", false);
		
    	cal.setTimeInMillis(e.getGuild().getTimeCreated().toInstant().toEpochMilli());
		String creatdate = date.format(cal.getTime()).toString();   
		String creattime = time.format(cal.getTime()).toString();   
    	eb.addField("Created", String.format( "**%s** | **%s** UTC", creatdate, creattime), false);
    	
		cal.setTimeInMillis(API.getMember(e.getGuild(), e.getJDA().getSelfUser()).getTimeJoined().toInstant().toEpochMilli());
		String joindate = date.format(cal.getTime()).toString();   
		String jointime = time.format(cal.getTime()).toString();    		
    	eb.addField("Bot connected", String.format( "**%s** | **%s** UTC", joindate, jointime), false);
    	
        eb.addField("Custom invite URL", (!API.isPartnered(e.getGuild()) ? "Guild is not partnered" : e.getGuild().retrieveVanityUrl().complete()), false);
    	
    	String s = ""; //by @maasterkoo
    	
        int i = 0;
        
        List<Role> roles = e.getGuild().getRoles().stream().collect(Collectors.toCollection(ArrayList::new));
        roles.remove(e.getGuild().getPublicRole());
        
        for (Role role : roles) {
        	
            i++;
            
            if (i == roles.size()) {
            	
                s += role.getName();
                
            }    
            
            else {
            	
                s += role.getName() + ", ";
             
            }    
            
        }
        
    	eb.addField("Roles [**" + i + "**]", s, false);
    	       	
		API.sendMessage(e.getChannel(), eb.build());
		
	}

	@Override
	public String help() {

		return "Shows you info about this guild";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {

		return;
		
	}	

}