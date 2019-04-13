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
	public final boolean called(final GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {
		
		final String subreddit = e.getMessage().getContentRaw().substring(9);
		
		final Credentials credentials = Credentials.script("canelex_", Secrets.redditPass, Secrets.redditClientId, Secrets.redditSecret);
		final UserAgent ua = new UserAgent("bot", "me.canelex.Spidey", "STABLE", "canelex_");
		final OkHttpNetworkAdapter adapter = new OkHttpNetworkAdapter(ua);
		final RedditClient reddit = OAuthHelper.automatic(adapter, credentials);
		
	    try {
	    	
	    	final Subreddit sr = reddit.subreddit(subreddit).about();			
			
	    	final int subs = sr.getSubscribers();
	    	final int active = sr.getAccountsActive();
	    	final String desc = sr.getPublicDescription();
				
	    	final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
			eb.setAuthor(sr.getTitle(), "https://reddit.com/r/" + subreddit, "https://i.ymastersk.net/LRjhvy");
			eb.setColor(16727832);				
			eb.addField("Subscribers", "**" + subs + "**", false);
			eb.addField("Active users", "**" + active + "**", false);
			eb.addField("Description", (desc.length() == 0 ? "**None**" : desc), false);
			eb.addField("NSFW", "**" + (sr.isNsfw() ? "Yes" : "No") + "**", false);
				
			API.sendMessage(e.getChannel(), eb.build());		    	
	    	
	    }		
	    
	    catch (final NullPointerException ex) {
	    	
	    	API.sendMessage(e.getChannel(), ":no_entry: Subreddit not found.", false);
	    	
	    }
		
	}

	@Override
	public final String help() {
		
		return "Shows you info about entered subreddit. For example `s!reddit PewdiepieSubmissions`.";
		
	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}
	
}