package me.canelex.Spidey.commands;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GuildCommand implements ICommand {
	
	private final Locale locale = new Locale("en", "EN");
	private final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
	private final SimpleDateFormat date = new SimpleDateFormat("EE, d.LLL Y", locale);
	private final SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", locale);
	
	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());         	
    	eb.setColor(Color.ORANGE);
    	eb.setThumbnail(e.getGuild().getIconUrl());
    	
    	eb.addField("Server Name", e.getGuild().getName(), true);
    	eb.addField("Server ID", e.getGuild().getId(), true);
    	    		
		eb.addField("Owner Name", e.getGuild().getOwner().getUser().getAsTag(), true);
		eb.addField("Owner ID", e.getGuild().getOwnerId(), true);
		
		eb.addField("Text Channels", "" + e.getGuild().getTextChannelCache().size(), true);
		eb.addField("Voice Channels", "" + e.getGuild().getVoiceChannelCache().size(), true);
		
    	eb.addField("Members", "" + e.getGuild().getMemberCache().size(), true);	    	
    	eb.addField("Verification Level", e.getGuild().getVerificationLevel().name(), true);
    	
    	final List<Role> roles = e.getGuild().getRoleCache().stream().collect(Collectors.toCollection(ArrayList::new));
        roles.remove(e.getGuild().getPublicRole());
        
    	eb.addField("Role count", "" + roles.size(), true);
    	eb.addField("Emote Count", "" + e.getGuild().getEmoteCache().size(), true);
    	
    	eb.addField("Region", e.getGuild().getRegionRaw(), true);    	    	
		
    	cal.setTimeInMillis(e.getGuild().getTimeCreated().toInstant().toEpochMilli());
    	final String creatdate = date.format(cal.getTime());
    	final String creattime = time.format(cal.getTime());
    	eb.addField("Creation", String.format( "%s | %s", creatdate, creattime), true);   
    	
        eb.addField("Custom invite URL", (!API.isPartnered(e.getGuild()) ? "Guild is not partnered" : "discord.gg/" + e.getGuild().retrieveVanityUrl().complete()), true);
        
        StringBuilder st = new StringBuilder();
        
        int ec = 0;
        final long an = e.getGuild().getEmotes().stream().filter(em -> em.isAnimated()).count();
        
        for (final Emote emote : e.getGuild().getEmotes()) {
        	
        	ec++;
        	
        	if (ec == e.getGuild().getEmotes().size()) {
        		
        		st.append(emote.getAsMention());
        		
        	}
        	
        	else {
        		
        		st.append(emote.getAsMention() + " ");
        		
        	}
        	
        }
        
        if (st.length() > 1024) {
        	
        	eb.addField(String.format((ec == 0) ? "Emotes (**0**)" : "Emotes (**%s** | **%s** animated)", ec, an), "Limit exceeded", false);
        	
        }
        
        else {
        	
        	eb.addField(String.format((ec == 0) ? "Emotes (**0**)" : "Emotes (**%s** | **%s** animated)", ec, an), (st.toString().length() == 0) ? "None" : st.toString(), false);
        	
        }                         	
    	       	
		API.sendMessage(e.getChannel(), eb.build());
		
	}

	@Override
	public final String help() {

		return "Shows you info about this guild";
		
	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}

}