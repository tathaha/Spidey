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
	public final boolean called(final GuildMessageReceivedEvent e) {
		
		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {
		
		final List<Member> tonline = e.getGuild().getMemberCache().stream().filter(member -> member.getOnlineStatus() == OnlineStatus.ONLINE || member.getOnlineStatus() == OnlineStatus.IDLE || member.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB).collect(Collectors.toList());
		final long bonline = tonline.stream().filter(m -> m.getUser().isBot()).count();        	
		final long total = e.getGuild().getMemberCache().size();     	
		final long online = tonline.size();
		final long bots = e.getGuild().getMemberCache().stream().filter(member -> member.getUser().isBot()).count();
		final long ponline = online - bonline;
		final long monline = e.getGuild().getMemberCache().stream().filter(m -> API.isMobile(m)).count();
		final long wonline = e.getGuild().getMemberCache().stream().filter(m -> API.isWeb(m)).count();
		final long donline = e.getGuild().getMemberCache().stream().filter(m -> API.isDesktop(m)).count();    	
    	
		final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
    	eb.setAuthor("MEMBERCOUNT");
    	eb.setColor(Color.WHITE);
    	eb.addField("Total", "**" + total + "**", true);        	
    	eb.addField("People", "**" + (total - bots) + "**", true);
    	eb.addField("Bots", "**" + bots + "**", true);
    	eb.addField("Total online", "**" + online + "**", true);
    	eb.addField("People online", "**" + ponline + "**", true);
    	eb.addField("Bots online", "**" + bonline + "**", true);  
    	eb.addField("Desktop users online", "**" + donline + "**", true);
    	eb.addField("Mobile users online", "**" + monline + "**", true);    	
    	eb.addField("Web users online", "**" + wonline + "** (includes bots)", true);
       	API.sendMessage(e.getChannel(), eb.build());		
		
	}

	@Override
	public final String help() {
		
		return "Shows you membercount of your guild";
		
	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}
	
}