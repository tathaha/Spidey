package me.canelex.Spidey.commands;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import me.canelex.Spidey.Core;
import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class HelpCommand implements ICommand {
	
	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor())
				.setColor(Color.WHITE)
				.setAuthor("Spidey's Commands", "https://github.com/caneleex/Spidey", e.getJDA().getSelfUser().getEffectiveAvatarUrl());
		
		final StringBuilder sb = new StringBuilder();		
		
		final HashMap<String, ICommand> commands = new HashMap<>();
		
		for (final Map.Entry<String, ICommand> entry : Core.commands.entrySet()) {
			
			commands.put(entry.getKey(), entry.getValue());
			
		}
		
		commands.remove("yt");
				
		for (final String cmd : commands.keySet()) {
				
			if (cmd.equals("g")) {
					
				sb.append("`s!" + cmd + "` | `s!yt` - " + Core.commands.get(cmd).help() + "\n");
					
			}
				
			else {
					
				sb.append("`s!" + cmd + "` - " + Core.commands.get(cmd).help() + "\n");
					
			}
				
			final String help = sb.toString();
			eb.setDescription(help);
			
		}
		
		API.sendMessage(e.getChannel(), eb.build());
		
	}

	@Override
	public final String help() {

		return "Shows you this message";
		
	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}

}