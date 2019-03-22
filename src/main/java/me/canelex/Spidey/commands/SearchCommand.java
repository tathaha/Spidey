package me.canelex.Spidey.commands;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.objects.search.GoogleSearch;
import me.canelex.Spidey.objects.search.SearchResult;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SearchCommand implements ICommand {

	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {
		
		String filter = null;
		final String[] args = e.getMessage().getContentRaw().split("\\s+");
		
		switch (args[0]) {
				
			case "s!g":
				
				break;				
				
			case "s!yt":
				
				filter = "site:youtube.com";
				break;			
				
			default:
				
				return;
				
		}

		final List<SearchResult> results = GoogleSearch.performSearch(
				"015021391643023377625:kq7ex3xgvoq",
				StringUtils.join(args, "+", 1, args.length)
						+ ((filter != null) ? ("+" + filter) : ""));

		API.sendMessage(e.getChannel(), results.get(0).getSuggestedReturn(), false);		
		
	}

	@Override
	public final String help() {

		return "Allows you to search Google or YouTube";
		
	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {
		
		return;
		
	}

}