package me.canelex.Spidey;

import java.awt.Color;
import java.util.List;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.Ban;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Events extends ListenerAdapter {		 
	
	@Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		if (e.getAuthor().isBot()) {
			
			return;
			
		}
		
		if (e.getMessage().getContentRaw().startsWith("s!") && !e.getAuthor().isBot()){
			
			Core.handleCommand(Core.parser.parse(e.getMessage().getContentRaw(), e));
			return;
			
		}       	
        
	}
	
	@Override
	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent e) {
		
		Guild guild = e.getGuild();
		Role muted = guild.getRolesByName("Muted", false).get(0);
		
		if (e.getRoles().contains(muted)) {
			
			if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) == null) {
				
				return;
				
			}
			
			else {
				
				TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));				
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("UNMUTE");
				eb.setColor(Color.GREEN);			
		        eb.setThumbnail(e.getUser().getEffectiveAvatarUrl());						
				eb.addField("User", "**" + e.getUser().getAsTag() + "**", false);
				API.sendMessage(log, eb.build());				
				
			}
			
		}
		
	}
	
	@Override
    public void onGuildBan(GuildBanEvent e) {
		
		User user = e.getUser();
		Guild guild = e.getGuild();
		
		if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) == null) {
			
			return;
			
		}
		
		else {
			
			TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));				
	        Ban ban = guild.retrieveBan(user).complete();
	        List<AuditLogEntry> auditbans = guild.retrieveAuditLogs().type(ActionType.BAN).complete();
	        User banner = auditbans.get(0).getUser();
	        EmbedBuilder eb = new EmbedBuilder();
	        eb.setTitle("NEW BAN");        
	        eb.setThumbnail(user.getEffectiveAvatarUrl());                
	        eb.setColor(Color.RED);
	        eb.addField("User", "**" + user.getAsTag() + "**", true);
	        eb.addField("ID", "**" + user.getId() + "**", true);        
	        eb.addField("Moderator", banner.getAsMention(), true);        
	        eb.addField("Reason", "**" + (ban.getReason() == null ? "Unknown" : ban.getReason()) + "**", true);
	               
			API.sendMessage(log, eb.build());			
			
		}        
		
	}	
	
	@Override
    public void onGuildUnban(GuildUnbanEvent e) {
		
		User user = e.getUser();
		Guild guild = e.getGuild();		
		
		if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) == null) {
			
			return;
			
		}
		
		else {
			
			TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));			
	        EmbedBuilder eb = new EmbedBuilder();
	        eb.setTitle("UNBAN");
	        eb.setColor(Color.GREEN);        
	        eb.setThumbnail(user.getEffectiveAvatarUrl());                      
	        eb.addField("User", "**" + user.getAsTag() + "**", true);
	        eb.addField("ID", "**" + user.getId() + "**", true);        
			API.sendMessage(log, eb.build());			
			
		}        
		
	}	
	
	@Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
		
		User user = e.getUser();
		Guild guild = e.getGuild();	
		
		if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) == null) {
			
			return;
			
		}
		
		else {
			
			TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
	        EmbedBuilder eb = new EmbedBuilder();
	        eb.setTitle("USER HAS LEFT");        
	        eb.setThumbnail(user.getEffectiveAvatarUrl());                
	        eb.setColor(Color.RED);
	        eb.addField("User", "**" + user.getAsTag() + "**", true);
	        eb.addField("ID", "**" + user.getId() + "**", true);
	        API.sendMessage(log, eb.build());			
			
		}                
		
	}	
	
	@Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		
		User user = e.getUser();
		Guild guild = e.getGuild();		
		
		if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) == null) {
			
			return;
			
		}
		
		else {
			
			TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));					
	        EmbedBuilder eb = new EmbedBuilder();
	        eb.setTitle("USER HAS JOINED");        
	        eb.setThumbnail(user.getEffectiveAvatarUrl());                     
	        eb.setColor(Color.GREEN);
	        eb.addField("User", "**" + user.getAsTag() + "**", true);
	        eb.addField("ID", "**" + user.getId() + "**", true);        
	        API.sendMessage(log, eb.build());			
			
		}    
		
	}	
	
	@Override
	public void onGuildLeave(GuildLeaveEvent e) {
		
		if (MySQL.isInDatabase(e.getGuild().getIdLong())) {
			
			MySQL.removeData(e.getGuild().getIdLong());
			
		}
		
	}
		
}
