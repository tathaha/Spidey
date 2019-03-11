package me.canelex.Spidey.commands;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MembercountCommand implements ICommand {

	@Override
	public boolean called(GuildMessageReceivedEvent e) {
		
		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {
		
    	List<Member> tonline = e.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus() == OnlineStatus.ONLINE || member.getOnlineStatus() == OnlineStatus.IDLE || member.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB).collect(Collectors.toList());
    	long bonline = tonline.stream().filter(m -> m.getUser().isBot()).count();        	
    	long total = e.getGuild().getMemberCache().size();     	
    	long online = tonline.size();
    	long bots = e.getGuild().getMembers().stream().filter(member -> member.getUser().isBot()).count();
    	long ponline = online - bonline;
    	
    	EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
    	eb.setTitle("MEMBERCOUNT");
    	eb.setColor(Color.WHITE);
    	eb.addField("Total", "**" + total + "**", true);        	
    	eb.addField("People", "**" + (total - bots) + "**", true);
    	eb.addField("Bots", "**" + bots + "**", true);
    	eb.addField("Total online", "**" + online + "**", true);
    	eb.addField("People online", "**" + ponline + "**", true);
    	eb.addField("Bots online", "**" + bonline + "**", true);        	
       	API.sendMessage(e.getChannel(), eb.build());		
		
	}

	@Override
	public String help() {
		
		return "Shows you membercount of your guild";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		
		return;
		
	}
	
}