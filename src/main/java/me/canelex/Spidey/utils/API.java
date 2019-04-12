package me.canelex.Spidey.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ClientType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class API {		
	
	public static final boolean hasPerm(final Member toCheck, final Permission perm) {
		
		return toCheck.hasPermission(perm);
		
	}
	
	public static final void sendMessage(final TextChannel ch, final String toSend, final boolean isSpoiler) {
		
		if (isSpoiler) {
			
			ch.sendMessage("||" + toSend + "||").queue();
			
		}
		
		else {
			
			ch.sendMessage(toSend).queue();				
			
		}
		
	}

	public static final void sendMessage(final TextChannel ch, final MessageEmbed embed) {
		
		ch.sendMessage(embed).queue();		
		
	}		

	public static final void sendPrivateMessage(final User user, final String toSend, final boolean isSpoiler) {

		user.openPrivateChannel().queue(channel -> {
			
			if (isSpoiler) {
				
				channel.sendMessage("||" + toSend + "||").queue();
				
			}
			
			else {
				
				channel.sendMessage(toSend).queue();				
				
			}							
			
		});		
		
	}	
	
	public static final boolean hasRole(final Member member, final Role r) {
		
		return member.getRoles().contains(r);
		
	}
	
	public static final Role getRoleById(final Guild g, final long id) {
		
		return g.getRoleById(id);
		
	}		
	
	public static final void deleteMessage(final Message msg) {
		
		msg.delete().queue();
		
	}
	
	public static final boolean isPartnered(final Guild g) {
		
		return g.getFeatures().contains("VIP_REGIONS"); 	
		
	}
	
    public static final String replaceLast(final String text, final String regex, final String replacement) {
    	
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
        
    }  	
    
    public static final EmbedBuilder createEmbedBuilder(final User u) { //by maasterkoo
    	
        return new EmbedBuilder().setFooter("Command executed by " + u.getAsTag(), u.getEffectiveAvatarUrl());
        
    } 
    
    public static final String getInviteUrl(final long guildId) {
    	
    	return String.format("https://discordapp.com/oauth2/authorize?client_id=468523263853592576&guild_id=%s&scope=bot&permissions=268446900", guildId);
    	
    }
    
    public static final void sendPrivateMessageFormat(final User u, final String message, final boolean isSpoiler, final Object... args) {
    	
    	sendPrivateMessage(u, String.format(message, args), false);
    	
    }    
    
    public static final boolean isWeb(final Member member) {
    	
    	return member.getOnlineStatus(ClientType.WEB) != OnlineStatus.OFFLINE;
    	
    }    
    
    public static final boolean isDesktop(final Member member) {
    	
    	return member.getOnlineStatus(ClientType.DESKTOP) != OnlineStatus.OFFLINE;
    	
    }        
    
    public static final boolean isMobile(final Member member) {
    	
    	return member.getOnlineStatus(ClientType.MOBILE) != OnlineStatus.OFFLINE;
    	
    }    

}
