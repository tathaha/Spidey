package me.canelex.Spidey.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;


public class API {
	
	public static void addRole(final Member m, final Role r) {
		
		Guild g = r.getGuild();
		g.getController().addSingleRoleToMember(m, r).submit();
		
	}	
	
	public static void removeRole(final Member m, final Role r) {
		
		Guild g = r.getGuild();
		g.getController().removeSingleRoleFromMember(m, r).submit();
		
	}		
	
	
	public static boolean hasPerm(final Guild g, final User toCheck, final Permission perm) {
		
		return g.getMember(toCheck).hasPermission(perm);
		
	}
	
	public static void sendMessage(final MessageChannel ch, final String toSend, final boolean isSpoiler) {
		
		if (isSpoiler) {
			
			ch.sendMessage("||" + toSend + "||").submit();
			
		}
		
		else {
			
			ch.sendMessage(toSend).submit();				
			
		}
		
	}

	public static void sendMessage(final MessageChannel ch, final MessageEmbed embed) {
		
		ch.sendMessage(embed).submit();		
		
	}	
	
	public static void sendFile(final MessageChannel ch, final File file) {
		
		ch.sendFile(file).submit();
		
	}		
	
	public static void sendImage(final MessageChannel ch, final String link, final boolean isSpoiler) {
		
		try {
			
			InputStream in = new BufferedInputStream(new URL(link).openStream());	
			
			if (isSpoiler) {
				
				ch.sendFile(in, "SPOILER_" + link.substring(link.lastIndexOf("/") + 1)).submit();					
				
			}
			
			else {
				
				ch.sendFile(in, link.substring(link.lastIndexOf("/") + 1)).submit();					
				
			}
								
			in.close();
			
		}		
		
		catch (IOException ex) {
			
			ex.printStackTrace();
			
		}
		
	}	

	public static void sendPrivateMessage(final User user, final String toSend, final boolean isSpoiler) {

		user.openPrivateChannel().queue(channel -> {
			
			if (isSpoiler) {
				
				channel.sendMessage("||" + toSend + "||").submit();
				
			}
			
			else {
				
				channel.sendMessage(toSend).submit();				
				
			}							
			
		});		
		
	}
	
	public static void sendPrivateMessage(final User user, final MessageEmbed embed) {

		user.openPrivateChannel().queue(channel -> channel.sendMessage(embed).submit());		
		
	}	
	
	public static boolean hasRole(final Member member, final Role r) {
		
		return member.getRoles().contains(r);
		
	}
	
	public static Member getMember(final Guild g, final User u) {
		
		return g.getMember(u);
		
	}
	
	public static User getUser(final Member m) { 
		
		return m.getUser();
		
	}
	
	public static Role getRoleById(final Guild g, final String id) {
		
		return g.getRoleById(id);
		
	}		
	
	public static void deleteMessage(final Message msg) {
		
		msg.delete().submit();
		
	}
	
	public static boolean isPartnered(final Guild g) {
		
		return g.getFeatures().contains("VIP_REGIONS"); 	
		
	}
	
    public static String replaceLast(final String text, final String regex, final String replacement) {
    	
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
        
    }  	
    
    public static EmbedBuilder createEmbedBuilder(final User u) { //by maasterkoo
    	
        return new EmbedBuilder().setFooter("Command executed by " + u.getAsTag(), u.getEffectiveAvatarUrl());
        
    }    

}
