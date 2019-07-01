package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.objects.search.GoogleSearch;
import me.canelex.spidey.objects.search.SearchResult;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unused")
public class SearchCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final String[] args = e.getMessage().getContentRaw().split("\\s+");

		final SearchResult result = GoogleSearch.performSearch(
				StringUtils.join(args, "+", 1, args.length));

		Utils.sendMessage(e.getChannel(), result.getContent(), false);

	}

	@Override
	public final String getDescription() { return "Allows you to search for results on Google"; }
	@Override
	public final boolean isAdmin() { return false; }
	@Override
	public final String getInvoke() { return "g"; }
	@Override
	public final Category getCategory() { return Category.MISC; }
	@Override
	public final String getUsage() { return "s!g <query>"; }

}