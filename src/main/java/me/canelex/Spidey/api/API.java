package me.canelex.Spidey.api;

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
		g.getController().addSingleRoleToMember(m, r).queue();
		
	}	
	
	public static void removeRole(Member m, Role r) {
		
		Guild g = r.getGuild();
		g.getController().removeSingleRoleFromMember(m, r).queue();
		
	}		
	
	
	public static boolean hasPerm(Guild g, User toCheck, Permission perm) {
		
		return g.getMember(toCheck).hasPermission(perm);
		
	}
	
	public static void sendMessage(MessageChannel ch, String toSend) {
		
		ch.sendMessage(toSend).queue();
		
	}

	public static void sendMessage(MessageChannel ch, MessageEmbed embed) {
		
		ch.sendMessage(embed).queue();		
		
	}	
	
	public static void sendFile(MessageChannel ch, File file) {
		
		ch.sendFile(file).queue();
		
	}		
	
	public static void sendImage(MessageChannel ch, String link) {
		
		try {
			
			InputStream in = new BufferedInputStream(new URL(link).openStream());		
			ch.sendFile(in, link.substring(link.lastIndexOf("/") + 1)).queue();						
			in.close();
			
		}		
		
		catch (IOException ex) {
			
			ex.printStackTrace();
			
		}
		
	}	

	public static void sendPrivateMessage(User user, String toSend) {

		user.openPrivateChannel().queue(channel -> channel.sendMessage(toSend).queue());		
		
	}
	
	public static void sendPrivateMessage(User user, MessageEmbed embed) {

		user.openPrivateChannel().queue(channel -> channel.sendMessage(embed).queue());		
		
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
		
		msg.delete().queue();
		
	}

}
