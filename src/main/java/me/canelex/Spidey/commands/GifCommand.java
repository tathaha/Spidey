package me.canelex.Spidey.commands;

import at.mukprojects.giphy4j.Giphy;
import at.mukprojects.giphy4j.entity.search.SearchFeed;
import at.mukprojects.giphy4j.exception.GiphyException;
import me.canelex.Spidey.Secrets;
import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GifCommand implements ICommand {

	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {

		return true;

	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final String query = e.getMessage().getContentRaw().substring(6);

		try {

			final Giphy giphy = new Giphy(Secrets.giphykey);
			final SearchFeed feed = giphy.search(query, 1, 0);

			API.sendMessage(e.getChannel(), "Gif matching **" + query + "**: " + feed.getDataList().get(0).getImages().getOriginal().getUrl(), false);

		}

		catch (final GiphyException ex) {}

	}

	@Override
	public final String help() {

		return "Sends a gif matching your query";

	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}

}