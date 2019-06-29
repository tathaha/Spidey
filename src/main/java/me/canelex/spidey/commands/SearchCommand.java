package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.objects.search.GoogleSearch;
import me.canelex.spidey.objects.search.SearchResult;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class SearchCommand implements ICommand {

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

		final SearchResult result = GoogleSearch.performSearch(
				"015021391643023377625:kq7ex3xgvoq",
				StringUtils.join(args, "+", 1, args.length)
						+ ((filter != null) ? ("+" + filter) : ""));

		Utils.sendMessage(e.getChannel(), result.getContent(), false);

	}

	@Override
	public final String help() { return "Allows you to search for results on Google or YouTube"; }
	@Override
	public final boolean isAdmin() { return false; }
	@Override
	public final String invoke() { return "g"; }
	@Override
	public List<String> aliases() { return Collections.singletonList("yt"); }

}