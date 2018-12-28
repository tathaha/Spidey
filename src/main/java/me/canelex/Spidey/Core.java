package me.canelex.Spidey;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

public class Core {	
							
	public static void main( String[] args ) throws Exception {
    	
    	@SuppressWarnings("unused")
		JDA jda = new JDABuilder(AccountType.BOT)
    			.setToken(Secrets.token)
    			.addEventListener(new Events())
    			.setGame(Game.streaming("s!help", "https://twitch.tv/canelex_"))    			
    			.build().awaitReady();               
        
    }                        
    
}
