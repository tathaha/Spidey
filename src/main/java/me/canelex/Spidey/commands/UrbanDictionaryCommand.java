package me.canelex.Spidey.commands;

import java.net.URLEncoder;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.IEmoji;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UrbanDictionaryCommand implements ICommand {

	@Override
	public boolean called(GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {	
		
		final String query = e.getMessage().getContentRaw().substring(5);
		
	    try {
	    	
            Future<HttpResponse<JsonNode>> future = Unirest.get("http://api.urbandictionary.com/v0/define?term=" + URLEncoder.encode(query, "UTF-8")).asJsonAsync();
            HttpResponse<JsonNode> json = future.get(30, TimeUnit.SECONDS);
            JSONArray list = json.getBody().getObject().getJSONArray("list");
            
            if (list.length() == 0) {
            	
    	    	API.sendMessage(e.getChannel(), ":no_entry: Query not found", false);            	
            	
            }
            
            JSONObject item = list.getJSONObject(0);
            final String result = String.format("Urban Dictionary \n\n"
                            + "Definition for **%s**: \n"
                            + "```\n"
                            + "%s\n"
                            + "```\n"
                            + "**example**: \n"
                            + "%s" + "\n\n"
                            + "_by %s (" + IEmoji.like + "%s  " + IEmoji.dislike + "%s)_"
                    , item.getString("word"), item.getString("definition"), item.getString("example"),
                    item.getString("author"), item.getInt("thumbs_up"), item.getInt("thumbs_down"));
            
            API.sendMessage(e.getChannel(), result, false);
	    	
	    }		
	    
	    catch (Exception ex) {
	    	
	    	ex.printStackTrace();
	    	API.sendMessage(e.getChannel(), ":no_entry: A problem has occured: **" + ex.getMessage() + "**", false);
	    	
	    }
		
	}

	@Override
	public String help() {

		return "Returns a definition of your query from Urban Dictionary";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		
		return;
		
	}
	
}