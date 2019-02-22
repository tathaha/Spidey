package me.canelex.Spidey.commands;

import java.awt.Color;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import me.canelex.Spidey.objects.command.Command;
import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.PermissionError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class EvalCommand implements Command {
	
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("groovy"); 	
	
	@Override
	public boolean called(GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {

		final String neededPerm = "ADMINISTRATOR";	
		
		if (!API.hasPerm(e.getMember(), Permission.valueOf(neededPerm))) {
			
			API.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);      			
			
		}  
		
		else {
			
			try {
				
    			String toEval = e.getMessage().getContentRaw().substring(7);
    			EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
    			engine.put("e", e);
    			engine.put("guild", e.getGuild());
    			engine.put("author", e.getAuthor());
    			engine.put("jda", e.getJDA());
    			engine.put("channel", e.getChannel());
    			eb.setTitle("CODE EVALUATION WAS SUCCESSFUL");
    			eb.addField("Result", "```java\n" + engine.eval(toEval) + "\n```", true);
    			eb.setColor(Color.WHITE);
    			API.sendMessage(e.getChannel(), eb.build());
				
			}
			
			catch (ScriptException ex) {
				
				ex.printStackTrace();
				EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
				eb.setTitle("CODE EVALUATION WAS UNSUCCESSFUL");
				eb.addField("Problem", ex.getMessage(), true);
				eb.setColor(Color.RED);
				API.sendMessage(e.getChannel(), eb.build());
				
			}    			
			
		}
		
	}

	@Override
	public String help() {

		return "Evaluates java code you entered";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {

		return;
		
	}	

}