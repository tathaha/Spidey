package me.canelex.Spidey.commands;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import me.canelex.Spidey.objects.command.Command;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PingCommand implements Command {
	
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("groovy"); 	
	
	@Override
	public boolean called(GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {

		API.sendMessage(e.getChannel(), "**Gateway**/**WebSocket**: **" + e.getJDA().getGatewayPing() + "**ms\n**REST**: **" + e.getJDA().getRestPing().complete() + "**ms", false);
		
	}

	@Override
	public String help() {

		return "Shows you ping info";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {

		return;
		
	}	

}