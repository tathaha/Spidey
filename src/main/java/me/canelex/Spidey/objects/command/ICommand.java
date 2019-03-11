package me.canelex.Spidey.objects.command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface ICommand {
	
	String help = "";
	boolean called(GuildMessageReceivedEvent e);
	void action(GuildMessageReceivedEvent e);
	String help();
	void executed(boolean success, GuildMessageReceivedEvent e);	

}