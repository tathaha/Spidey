package me.canelex.Spidey.commands;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import me.canelex.Spidey.objects.command.Command;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UserCommand implements Command {
	
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
		
		if (e.getMessage().getMentionedUsers().isEmpty()) {
			
			EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
			
			eb.setAuthor("USER INFO - " + e.getAuthor().getAsTag());
			eb.setColor(Color.WHITE);
			eb.setThumbnail(e.getAuthor().getEffectiveAvatarUrl());    			
			eb.addField("ID", "**" + e.getAuthor().getId() + "**", false);
			
			eb.addField("Nickname for this guild", "**" + (e.getMember().getNickname() == null ? "None" : e.getMember().getNickname()) + "**", false);
			
        	cal.setTimeInMillis(e.getAuthor().getTimeCreated().toInstant().toEpochMilli());
    		String creatdate = date.format(cal.getTime()).toString();   
    		String creattime = time.format(cal.getTime()).toString(); 
    		
    		eb.addField("Account created", String.format( "**%s** | **%s** UTC", creatdate, creattime), false);
    		
        	cal.setTimeInMillis(e.getMember().getTimeJoined().toInstant().toEpochMilli());
    		String joindate = date.format(cal.getTime()).toString();   
    		String jointime = time.format(cal.getTime()).toString(); 
    		
    		eb.addField("User joined", String.format( "**%s** | **%s** UTC", joindate, jointime), false);
    		
    		if (e.getMember().getRoles().size() == 0) {
    			
            	eb.addField("Roles [**0**]", "None", false);         			
    			
    		}
    		
    		else {
    		
                int i = 0;
            	String s = "";
            		
                for (Role role : e.getMember().getRoles()) {
                    	
                     i++;
                        
                     if (i == e.getMember().getRoles().size()) {
                        	
                         s += role.getName();
                            
                     }    
                        
                     else {
                        	
                         s += role.getName() + ", ";
                         
                      }    
                        
                }  
                
                eb.addField("Roles [**" + i + "**]", s, false);                    
    			
    		}        		                	                	 
        	
        	API.sendMessage(e.getChannel(), eb.build());
			
		}    		
		
		else {
			
			User user = e.getMessage().getMentionedUsers().get(0);
			Member member = API.getMember(e.getGuild(), user);
			
			EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
			
			eb.setAuthor("USER INFO - " + user.getAsTag());
			eb.setColor(Color.WHITE);    			
			eb.setThumbnail(user.getEffectiveAvatarUrl());
			eb.addField("ID", "**" + user.getId() + "**", false);
			
			eb.addField("Nickname for this guild", "**" + (member.getNickname() == null ? "None" : member.getNickname()) + "**", false);
			
        	cal.setTimeInMillis(user.getTimeCreated().toInstant().toEpochMilli());
    		String creatdate = date.format(cal.getTime()).toString();   
    		String creattime = time.format(cal.getTime()).toString(); 
    		
    		eb.addField("Account created", String.format( "**%s** | **%s** UTC", creatdate, creattime), false);
    		
        	cal.setTimeInMillis(member.getTimeJoined().toInstant().toEpochMilli());
    		String joindate = date.format(cal.getTime()).toString();   
    		String jointime = time.format(cal.getTime()).toString(); 
    		
    		eb.addField("User joined", String.format( "**%s** | **%s** UTC", joindate, jointime), false);       		
    			
    		if (member.getRoles().size() == 0) {
    			
            	eb.addField("Roles [**0**]", "None", false);         			
    			
    		}
    		
    		else {
    		
                int i = 0;
            	String s = "";
            		
                for (Role role : member.getRoles()) {
                    	
                     i++;
                        
                     if (i == member.getRoles().size()) {
                        	
                         s += role.getName();
                            
                     }    
                        
                     else {
                        	
                         s += role.getName() + ", ";
                         
                      }    
                        
                }  
                
                eb.addField("Roles [**" + i + "**]", s, false);                    
    			
    		}                                           		
    		
        	API.sendMessage(e.getChannel(), eb.build());            	
	
		}		
		
	}

	@Override
	public String help() {
		
		return "Shows info about you or mentioned user";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {

		return;
		
	}
	
}