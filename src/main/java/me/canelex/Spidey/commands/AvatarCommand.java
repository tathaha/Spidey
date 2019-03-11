package me.canelex.Spidey.commands;

import java.awt.Color;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AvatarCommand implements ICommand {

	@Override
	public boolean called(GuildMessageReceivedEvent e) {
		
		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {

		if (e.getMessage().getMentionedUsers().isEmpty()) {
			
			EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
			eb.setAuthor("Avatar of user " + e.getAuthor().getAsTag());
			eb.setImage(e.getAuthor().getEffectiveAvatarUrl());
			eb.setColor(Color.WHITE);
			API.sendMessage(e.getChannel(), eb.build());
			
		}
		
		else {
			
			User u = e.getMessage().getMentionedUsers().get(0);
			EmbedBuilder eb = API.createEmbedBuilder(u);
			eb.setAuthor("Avatar of user " + u.getAsTag()); 
			eb.setImage(u.getEffectiveAvatarUrl());
			eb.setColor(Color.WHITE);
			API.sendMessage(e.getChannel(), eb.build());    			
			
		}		
		
	}

	@Override
	public String help() {
		
		return "Shows avatar of you or mentioned user";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		
		return;
		
	}

}