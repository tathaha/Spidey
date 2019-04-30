package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PingCommand implements ICommand {
	
	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		API.sendMessage(e.getChannel(), "**Gateway**/**WebSocket**: **" + e.getJDA().getGatewayPing() + "**ms\n**REST**: **" + e.getJDA().getRestPing().complete() + "**ms", false);
		
	}

	@Override
	public final String help() {

		return "Shows you ping info";
		
	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}

}