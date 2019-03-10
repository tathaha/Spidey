package me.canelex.Spidey.commands;

import java.util.concurrent.TimeUnit;

import me.canelex.Spidey.objects.command.Command;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SlowmodeCommand implements Command {

	@Override
	public boolean called(GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {
		
        int seconds;

        String par = e.getMessage().getContentRaw().substring(11);
                
        if (par.equals("off") || par.equals("false")) {
        	
            seconds = 0;
            
        }
        
        else {
        	
            try {
            	
                seconds = Math.max(0, Math.min(Integer.parseInt(par), 120));
                
            }
            
            catch (NumberFormatException ignored) {
            	
                API.sendMessage(e.getChannel(), ":no_entry: Couldn't parse argument", false);
                return;
                
            }
            
        }		
        
        e.getChannel().getManager().setSlowmode(seconds).submit().thenRun(() -> e.getMessage().addReaction("✅").submit().thenRun(() -> {
        	
        	if (e.getMessage() != null) {
        		
        		e.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
        		
        	}
        	
        })).exceptionally(ex -> { 
        	
        	e.getChannel().sendMessage(":no_entry: An error has occured: " + ex.getMessage()).queue(m -> {
        		
        		if (m != null) {
        			
        			m.delete().queueAfter(5, TimeUnit.SECONDS);
        			
        		}
        		
        	});
        	
        	e.getMessage().addReaction("❌").queue();  
        	e.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
        	return null;
        	
        });
		
	}

	@Override
	public String help() {
		
		return "Sets a slowmode for channel. Example - `s!slowmode <seconds | off>`";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		
		return;
		
	}
	
}