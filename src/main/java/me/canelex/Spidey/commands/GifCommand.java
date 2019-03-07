package me.canelex.Spidey.commands;

import at.mukprojects.giphy4j.Giphy;
import at.mukprojects.giphy4j.entity.search.SearchFeed;
import at.mukprojects.giphy4j.exception.GiphyException;
import me.canelex.Spidey.Secrets;
import me.canelex.Spidey.objects.command.Command;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GifCommand implements Command {

	@Override
	public boolean called(GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {
		
		final String query = e.getMessage().getContentRaw().substring(6);
				
		try {
			
			Giphy giphy = new Giphy(Secrets.giphykey);
			SearchFeed feed = giphy.search(query, 1, 0);		
			
		    API.sendMessage(e.getChannel(), "Gif matching **" + query + "**: " + feed.getDataList().get(0).getImages().getOriginal().getUrl(), false);				
			
		}		
		
		catch (GiphyException ex) {
			
			ex.printStackTrace();
			
		}			 	
	      		
	}

	@Override
	public String help() {
		
		return "Sends a gif matching your query";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		
		return;
		
	}
	
}