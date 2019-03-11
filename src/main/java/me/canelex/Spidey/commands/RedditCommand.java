package me.canelex.Spidey.commands;

import me.canelex.Spidey.Secrets;
import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class RedditCommand implements ICommand {

	@Override
	public boolean called(GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {
		
		final String subreddit = e.getMessage().getContentRaw().substring(9);
		
		Credentials credentials = Credentials.script("canelex_", Secrets.redditPass, Secrets.redditClientId, Secrets.redditSecret);
		UserAgent ua = new UserAgent("bot", "me.canelex.Spidey", "STABLE", "canelex_");
		OkHttpNetworkAdapter adapter = new OkHttpNetworkAdapter(ua);
		RedditClient reddit = OAuthHelper.automatic(adapter, credentials);
		
	    try {
	    	
			Subreddit sr = reddit.subreddit(subreddit).about();			
			
			int subs = sr.getSubscribers();
			int active = sr.getAccountsActive();
			String desc = sr.getPublicDescription();
				
			EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
			eb.setAuthor(sr.getTitle(), "https://reddit.com/r/" + subreddit, "https://i.ymastersk.net/LRjhvy");
			eb.setColor(16727832);				
			eb.addField("Subscribers", "**" + subs + "**", false);
			eb.addField("Active users", "**" + active + "**", false);
			eb.addField("Description", (desc.length() == 0 ? "**None**" : desc), false);
			eb.addField("NSFW", "**" + (sr.isNsfw() ? "Yes" : "No") + "**", false);
				
			API.sendMessage(e.getChannel(), eb.build());		    	
	    	
	    }		
	    
	    catch (NullPointerException ex) {
	    	
	    	API.sendMessage(e.getChannel(), ":no_entry: Subreddit not found.", false);
	    	
	    }
		
	}

	@Override
	public String help() {
		
		return "Shows you info about entered subreddit. For example `s!reddit PewdiepieSubmissions`.";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		
		return;
		
	}
	
}