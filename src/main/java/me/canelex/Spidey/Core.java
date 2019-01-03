package me.canelex.Spidey;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Core {	
							
	public static void main( String[] args ) throws Exception {
    	
    	@SuppressWarnings("unused")
		JDA jda = new JDABuilder(AccountType.BOT)
    			.setToken(Secrets.token)
    			.addEventListeners(new Events())
    			.setActivity(Activity.streaming("s!help", "https://twitch.tv/canelex_"))    			
    			.build().awaitReady();               
        
    }                        
    
}
