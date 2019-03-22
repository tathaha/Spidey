package me.canelex.Spidey.commands;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PingCommand implements ICommand {
	
	final ScriptEngineManager manager = new ScriptEngineManager();
	final ScriptEngine engine = manager.getEngineByName("groovy"); 	
	
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
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {

		return;
		
	}	

}