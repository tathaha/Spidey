package me.canelex.Spidey.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;


public class API {
	
	public static void addRole(Member m, Role r) {
		
		Guild g = r.getGuild();
		g.getController().addSingleRoleToMember(m, r).submit();
		
	}	
	
	public static void removeRole(Member m, Role r) {
		
		Guild g = r.getGuild();
		g.getController().removeSingleRoleFromMember(m, r).submit();
		
	}		
	
	
	public static boolean hasPerm(Guild g, User toCheck, Permission perm) {
		
		return g.getMember(toCheck).hasPermission(perm);
		
	}
	
	public static void sendMessage(MessageChannel ch, String toSend, boolean isSpoiler) {
		
		if (isSpoiler) {
			
			ch.sendMessage("||" + toSend + "||").submit();
			
		}
		
		else {
			
			ch.sendMessage(toSend).submit();				
			
		}
		
	}

	public static void sendMessage(MessageChannel ch, MessageEmbed embed) {
		
		ch.sendMessage(embed).submit();		
		
	}	
	
	public static void sendFile(MessageChannel ch, File file) {
		
		ch.sendFile(file).submit();
		
	}		
	
	public static void sendImage(MessageChannel ch, String link, boolean isSpoiler) {
		
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

	public static void sendPrivateMessage(User user, String toSend, boolean isSpoiler) {

		user.openPrivateChannel().queue(channel -> {
			
			if (isSpoiler) {
				
				channel.sendMessage("||" + toSend + "||").submit();
				
			}
			
			else {
				
				channel.sendMessage(toSend).submit();				
				
			}							
			
		});		
		
	}
	
	public static void sendPrivateMessage(User user, MessageEmbed embed) {

		user.openPrivateChannel().queue(channel -> channel.sendMessage(embed).submit());		
		
	}	
	
	public static boolean hasRole(Member member, Role r) {
		
		return member.getRoles().contains(r);
		
	}
	
	public static Member getMember(Guild g, User u) {
		
		return g.getMember(u);
		
	}
	
	public static User getUser(Member m) { 
		
		return m.getUser();
		
	}
	
	public static Role getRoleById(Guild g, String id) {
		
		return g.getRoleById(id);
		
	}		
	
	public static void deleteMessage(Message msg) {
		
		msg.delete().submit();
		
	}
	
	public static boolean isPartnered(Guild g) {
		
		return g.getFeatures().contains("VIP_REGIONS"); 	
		
	}
	
    public static String replaceLast(final String text, final String regex, final String replacement) {
    	
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
        
    }  	

}
