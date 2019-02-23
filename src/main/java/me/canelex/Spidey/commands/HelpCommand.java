package me.canelex.Spidey.commands;

import java.awt.Color;

import me.canelex.Spidey.Core;
import me.canelex.Spidey.objects.command.Command;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class HelpCommand implements Command {
	
	@Override
	public boolean called(GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {

		EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor())
				.setColor(Color.WHITE)
				.setAuthor("Spidey's Commands", "https://github.com/caneleex/Spidey", e.getJDA().getSelfUser().getEffectiveAvatarUrl());
		
		StringBuilder sb = new StringBuilder();		
		
		for (String cmd : Core.commands.keySet()) {
			
			if (!Core.commands.get(cmd).help().equals(null)) {
				
				sb.append("`s!" + cmd + "` - " + Core.commands.get(cmd).help() + "\n");
				final String help = sb.toString();
				eb.setDescription(help);
				
			}
		}
		
		API.sendMessage(e.getChannel(), eb.build());
		
	}

	@Override
	public String help() {

		return "Shows you this message";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {

		return;
		
	}	

}