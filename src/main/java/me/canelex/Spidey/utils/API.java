package me.canelex.Spidey.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

public class API {		
	
	public static boolean hasPerm(final Member toCheck, final Permission perm) {
		
		return toCheck.hasPermission(perm);
		
	}
	
	public static void sendMessage(final TextChannel ch, final String toSend, final boolean isSpoiler) {
		
		if (isSpoiler) {
			
			ch.sendMessage("||" + toSend + "||").queue();
			
		}
		
		else {
			
			ch.sendMessage(toSend).queue();				
			
		}
		
	}

	public static void sendMessage(final TextChannel ch, final MessageEmbed embed) {
		
		ch.sendMessage(embed).queue();		
		
	}		

	private static void sendPrivateMessage(final User user, final String toSend) {

		user.openPrivateChannel().queue(channel -> channel.sendMessage(toSend).queue());
		
	}
	
	public static void deleteMessage(final Message msg) {
		
		msg.delete().queue();
		
	}
	
	public static boolean isPartnered(final Guild g) {
		
		return g.getFeatures().contains("VIP_REGIONS"); 	
		
	}
	
    public static  String replaceLast(final String text, final String regex, final String replacement) {
    	
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
        
    }  	
    
    public static EmbedBuilder createEmbedBuilder(final User u) { //by maasterkoo
    	
        return new EmbedBuilder().setFooter("Command executed by " + u.getAsTag(), u.getEffectiveAvatarUrl());
        
    } 
    
    public static String getInviteUrl(final long guildId) {
    	
    	return String.format("https://discordapp.com/oauth2/authorize?client_id=468523263853592576&guild_id=%s&scope=bot&permissions=268446900", guildId);
    	
    }
    
    public static void sendPrivateMessageFormat(final User u, final String message, final Object... args) {
    	
    	sendPrivateMessage(u, String.format(message, args));
    	
    }    
    
    public static boolean isWeb(final Member member) {
    	
    	return member.getOnlineStatus(ClientType.WEB) != OnlineStatus.OFFLINE;
    	
    }    
    
    public static boolean isDesktop(final Member member) {
    	
    	return member.getOnlineStatus(ClientType.DESKTOP) != OnlineStatus.OFFLINE;
    	
    }        
    
    public static boolean isMobile(final Member member) {
    	
    	return member.getOnlineStatus(ClientType.MOBILE) != OnlineStatus.OFFLINE;
    	
    }    

}
