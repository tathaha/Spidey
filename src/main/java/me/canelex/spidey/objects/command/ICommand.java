package me.canelex.spidey.objects.command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface ICommand {

	void action(final GuildMessageReceivedEvent e);
	String help();

}