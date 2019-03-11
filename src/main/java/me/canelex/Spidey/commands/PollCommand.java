package me.canelex.Spidey.commands;

import java.awt.Color;

import me.canelex.Spidey.MySQL;
import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.IEmoji;
import me.canelex.Spidey.utils.PermissionError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PollCommand implements ICommand {

	@Override
	public boolean called(GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {

		final String neededPerm = "BAN_MEMBERS";    		
		
		TextChannel log = e.getGuild().getTextChannelById(MySQL.getChannelId(e.getGuild().getIdLong()));	   		
		
		if (!API.hasPerm(e.getMember(), Permission.valueOf(neededPerm))) {
			
			API.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);      			
			
		}
		
		else {
			
    		String question = e.getMessage().getContentRaw().substring(7);
    		API.deleteMessage(e.getMessage());
    		e.getChannel().sendMessage("Poll: **" + question + "**").queue(m -> {
    			
    			m.addReaction(IEmoji.like).queue();
    			m.addReaction(IEmoji.shrug).queue();
    			m.addReaction(IEmoji.dislike).queue();
        		EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
        		eb.setTitle("NEW POLL");
        		eb.setColor(Color.ORANGE);             		
        		eb.addField("Question", "**" + question + "**", false);
        		eb.setFooter("Poll created by " + e.getAuthor().getAsTag(), e.getAuthor().getEffectiveAvatarUrl());             		
        		API.sendMessage(log, eb.build());
    			        			
    		});    			
    		   			
		}		
		
	}

	@Override
	public String help() {

		return "Creates a new poll";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {

		return;
		
	}

}