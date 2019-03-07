package me.canelex.Spidey.commands;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.SearchListResponse;
import com.mashape.unirest.http.Unirest;

import me.canelex.Spidey.Secrets;
import me.canelex.Spidey.objects.command.Command;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class YouTubeChannelCommand implements Command {
	
	Locale locale = new Locale("en", "EN");  
	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));        	
	SimpleDateFormat date = new SimpleDateFormat("EEEE, d.LLLL Y", locale);      
	SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", locale);  	

	@Override
	public boolean called(GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {
		
		final String channel = e.getMessage().getContentRaw().substring(12);
		
		try {
			
		    YouTube youtube = new YouTube.Builder(
		                new NetHttpTransport(),
		                new JacksonFactory(),
		                new HttpRequestInitializer() {
		                        public void initialize(HttpRequest request) throws IOException {
		                }
		        })
		    		
		        .setApplicationName("youtube-cmdline-search-sample")
		        .setYouTubeRequestInitializer(new YouTubeRequestInitializer(Secrets.youtubeapikey))
		        .build();

		        YouTube.Search.List search = youtube.search().list("snippet");
		        search.setQ(channel);
		        search.setType("channel");

		        SearchListResponse searchResponse = search.execute();
		        
		        if (!searchResponse.getItems().isEmpty()) {        	
		            	
		            String channelId = searchResponse.getItems().get(0).getSnippet().getChannelId();

		            YouTube.Channels.List channels = youtube.channels().list("snippet, statistics");
		            channels.setId(channelId);

		            Channel c = channels.execute().getItems().get(0);
		            
		            cal.setTimeInMillis(c.getSnippet().getPublishedAt().getValue());
		            
		    		String creatdate = date.format(cal.getTime()).toString();   
		    		String creattime = time.format(cal.getTime()).toString();   		            

		            EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
		            eb.setAuthor(c.getSnippet().getTitle(), "https://youtube.com/channel/" + channelId, "https://i.ymastersk.net/vo96zG");
		            eb.setColor(14765121);
		            eb.setThumbnail(c.getSnippet().getThumbnails().getHigh().getUrl());
		            eb.addField("Subscribers", "**" + c.getStatistics().getSubscriberCount() + "**", false);
		            eb.addField("Views", "**" + c.getStatistics().getViewCount() + "**", false);
		            eb.addField("Videos", "**" + c.getStatistics().getVideoCount() + "**", false);
		            eb.addField("Created", String.format( "**%s** | **%s** UTC", creatdate, creattime), false);
		            eb.addField("Description", (c.getSnippet().getDescription().length() == 0 ? "**None**" : "**" + c.getSnippet().getDescription() + "**"), false);
		            eb.addField("Country", (c.getSnippet().getCountry() == null ? "**Unknown**" : "**" + c.getSnippet().getCountry() + "**"), false);
		            eb.addField("Latest video", Unirest.get("https://beta.decapi.me/youtube/latest_video/?id=" + channelId).asStringAsync().get().getBody(), false);
		            
		            API.sendMessage(e.getChannel(), eb.build());
		            
		        }			
		        
		        else {
		        	
		        	API.sendMessage(e.getChannel(), ":no_entry: No results found", false);		        	
		        	
		        }
			
		}
		
		catch (Exception ex) {
			
			ex.printStackTrace();
			
		}				
		
	}

	@Override
	public String help() {

		return "Shows info about entered YouTube channel";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		
		return;
		
	}

}