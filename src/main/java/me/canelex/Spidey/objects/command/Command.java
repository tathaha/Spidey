package me.canelex.Spidey.objects.command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface Command {
	
	public String help = "";
	public boolean called(GuildMessageReceivedEvent e);
	public void action(GuildMessageReceivedEvent e);
	public String help();
	public void executed(boolean success, GuildMessageReceivedEvent e);	

}