package me.canelex.spidey.objects.command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface ICommand {
	
	String help = "";
	boolean called(final GuildMessageReceivedEvent e);
	void action(final GuildMessageReceivedEvent e);
	String help();
	void executed(final boolean success, final GuildMessageReceivedEvent e);	

}