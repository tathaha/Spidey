package me.canelex.spidey.commands;

import at.mukprojects.giphy4j.Giphy;
import at.mukprojects.giphy4j.entity.search.SearchFeed;
import at.mukprojects.giphy4j.exception.GiphyException;
import me.canelex.spidey.Secrets;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.API;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

public class GifCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final String query = e.getMessage().getContentRaw().substring(6);

		try {

			final Giphy giphy = new Giphy(Secrets.GIPHYKEY);
			final SearchFeed feed = giphy.search(query, 1, 0);

			API.sendMessage(e.getChannel(), "Gif matching **" + query + "**: " + feed.getDataList().get(0).getImages().getOriginal().getUrl(), false);

		}

		catch (final GiphyException ex) {
			LoggerFactory.getLogger(GifCommand.class).error("Exception!", ex);
		}

	}

	@Override
	public final String help() {

		return "Sends a gif matching your query";

	}

}