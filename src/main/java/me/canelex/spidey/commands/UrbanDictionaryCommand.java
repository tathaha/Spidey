package me.canelex.spidey.commands;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.API;
import me.canelex.spidey.utils.Emojis;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class UrbanDictionaryCommand implements ICommand {

	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {

		return true;

	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final String query = e.getMessage().getContentRaw().substring(5);

		try {

			final Future<HttpResponse<JsonNode>> future = Unirest.get("http://api.urbandictionary.com/v0/define?term=" + URLEncoder.encode(query, "UTF-8")).asJsonAsync();
			final HttpResponse<JsonNode> json = future.get(30, TimeUnit.SECONDS);
			final JSONArray list = json.getBody().getObject().getJSONArray("list");

			final JSONObject item = list.getJSONObject(0);
			final String result = String.format("Urban Dictionary \n\n"
							+ "Definition for **%s**: \n"
							+ "```\n"
							+ "%s\n"
							+ "```\n"
							+ "**example**: \n"
							+ "%s" + "\n\n"
							+ "_by %s (" + Emojis.like + "%s  " + Emojis.dislike + "%s)_"
					, item.getString("word"), item.getString("definition"), item.getString("example"),
					item.getString("author"), item.getInt("thumbs_up"), item.getInt("thumbs_down"));

			API.sendMessage(e.getChannel(), result, false);

		}

		catch (final Exception ex) {

			API.sendMessage(e.getChannel(), ":no_entry: Query not found.", false);

		}

	}

	@Override
	public final String help() {

		return "Returns a definition of your query from Urban Dictionary";

	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}

}