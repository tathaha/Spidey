package me.canelex.spidey.objects.command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Collections;
import java.util.List;

public interface ICommand {

	void action(final GuildMessageReceivedEvent e);
	String help();
	boolean isAdmin();
	String invoke();
    default List<String> aliases() { return Collections.emptyList(); }

}