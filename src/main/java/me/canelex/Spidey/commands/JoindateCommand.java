package me.canelex.Spidey.commands;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import me.canelex.Spidey.objects.command.Command;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class JoindateCommand implements Command {
	
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
    		
    		cal.setTimeInMillis(e.getMember().getTimeJoined().toInstant().toEpochMilli()); 
    		String joindate = date.format(cal.getTime()).toString();
    		String jointime = time.format(cal.getTime()).toString();        		
    		API.sendPrivateMessage(e.getAuthor(), String.format("Date and time of joining to guild **%s**: **%s** | **%s** UTC", e.getGuild().getName(), joindate, jointime), false);
    		
    	}
    	
    	else {
    		
    		List<User> mentioned = e.getMessage().getMentionedUsers();
    		
    		for (User user : mentioned) {
    			
    			Member member = API.getMember(e.getGuild(), user);
        		cal.setTimeInMillis(member.getTimeJoined().toInstant().toEpochMilli());
        		String joindate = date.format(cal.getTime()).toString();
        		String jointime = time.format(cal.getTime()).toString();            		
        		API.sendPrivateMessage(e.getAuthor(), "(**" + member.getEffectiveName() + "**) " + String.format("Date and time of joining to guild **%s**: **%s** | **%s** UTC", e.getGuild().getName(), joindate, jointime), false);          		
    		
    		}
    		
    	}		
		
	}

	@Override
	public String help() {
		
		return "Sends you PM containing joindate of you/mentioned user";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		
		return;
		
	}

}