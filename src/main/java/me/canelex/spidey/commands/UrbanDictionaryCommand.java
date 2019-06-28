package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.objects.json.UrbanDictionary;
import me.canelex.spidey.utils.Emojis;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@SuppressWarnings("unused")
public class UrbanDictionaryCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final String term = e.getMessage().getContentRaw().substring(5);

		try {

			final UrbanDictionary ud = new UrbanDictionary().getTerm(term);
			final String result = String.format("Urban Dictionary \n\n"
							+ "Definition for **%s**: \n"
							+ "```\n"
							+ "%s\n"
							+ "```\n"
							+ "**example**: \n"
							+ "%s" + "\n\n"
							+ "_by %s (" + Emojis.LIKE + "%s  " + Emojis.DISLIKE + "%s)_"
					, ud.getWord(), ud.getDefinition(), ud.getExample(),
					ud.getAuthor(), ud.getLikes(), ud.getDislikes());

			Utils.sendMessage(e.getChannel(), result, false);

		}

		catch (final Exception ex) {
			Utils.sendMessage(e.getChannel(), ":no_entry: Query not found.", false);
		}

	}

	@Override
	public final String help() { return "Returns a definition of your query from Urban Dictionary"; }
	@Override
	public final boolean isAdmin() { return false; }
	@Override
	public final String invoke() { return "ud"; }

}