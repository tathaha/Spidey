package me.canelex.Spidey.commands;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.PermissionError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class EvalCommand implements ICommand {
	
	final ScriptEngine engine = new ScriptEngineManager().getEngineByName("groovy");

	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final String toEval = e.getMessage().getContentRaw().substring(7);
		final String neededPerm = "ADMINISTRATOR";
		
		if (!API.hasPerm(e.getMember(), Permission.valueOf(neededPerm))) {
			
			API.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);    			
			
		}
		
		else {
			
			try {
				
				engine.put("e", e);
				engine.put("msg", e.getMessage());
				engine.put("author", e.getAuthor());
				engine.put("bot", e.getJDA().getSelfUser());
				engine.put("jda", e.getJDA());
				engine.put("guild", e.getGuild());
				
				e.getChannel().sendMessage("```java\n" + engine.eval(toEval) + "```").submit().thenRun(() -> e.getMessage().addReaction("✅").queue()).exceptionally(ex -> {
					
					e.getMessage().addReaction("❌").queue();
					return null;
					
				});
				
			}
			
			catch (final ScriptException ex) {
				
				ex.printStackTrace();
				
			}			
			
		}		
		
	}

	@Override
	public final String help() {

		return "Executes entered (java) code (importing needed)";
		
	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {
		
		return;
		
	}

}