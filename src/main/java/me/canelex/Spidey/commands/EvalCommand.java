package me.canelex.Spidey.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.tuple.Triple;

import me.canelex.Spidey.objects.command.Command;
import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.Engine;
import me.canelex.Spidey.utils.PermissionError;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.MessageBuilder.SplitPolicy;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;

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
			
	        final MessageBuilder builder = new MessageBuilder();

	        final Map<String, Object> shortcuts = new HashMap<>();

	        shortcuts.put("api", e.getMessage().getJDA());
	        shortcuts.put("jda", e.getMessage().getJDA());
	        shortcuts.put("event", e);

	        shortcuts.put("channel", e.getChannel());
	        shortcuts.put("server", e.getChannel().getGuild());
	        shortcuts.put("guild", e.getChannel().getGuild());

	        shortcuts.put("message", e.getMessage());
	        shortcuts.put("msg", e.getMessage());
	        shortcuts.put("me", e.getAuthor());
	        shortcuts.put("bot", e.getMessage().getJDA().getSelfUser());

	        final Triple<Object, String, String> result = Engine.GROOVY.eval(shortcuts, Collections.emptyList(), Engine.DEFAULT_IMPORTS, 30, e.getMessage().getContentRaw().substring(7));

	        if (result.getLeft() instanceof RestAction<?>) {
	        	
	            ((RestAction<?>) result.getLeft()).queue();	        	
	        	
	        }
	        
	        else if (result.getLeft() != null) {
	        	
	            builder.appendCodeBlock(result.getLeft().toString(), "java");
	            
	        }
	        
	        if (!result.getMiddle().isEmpty()) {
	        	
	            builder.append("\n").appendCodeBlock(result.getMiddle(), "java");
	            
	        }
	        
	        if (!result.getRight().isEmpty()) {
	        	
	            builder.append("\n").appendCodeBlock(result.getRight(), "java");
	            
	        }
	        
	        else {
	        	
	            for (final Message m : builder.buildAll(SplitPolicy.NEWLINE, SplitPolicy.SPACE, SplitPolicy.ANYWHERE)) {
	            	
	                e.getChannel().sendMessage(m).queue();   
	                e.getMessage().addReaction("✅").queue();	                
	                
	            }
	            
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