package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.objects.json.UrbanDictionary;
import me.canelex.spidey.utils.Emojis;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@SuppressWarnings("unused")
public class UrbanDictionaryCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final var term = e.getMessage().getContentRaw().substring(5);

		try {

			final var ud = new UrbanDictionary().getTerm(term);
			final var result = String.format("Urban Dictionary \n\n"
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
	public final String getDescription() { return "Returns the definition(s) of a phrase"; }
	@Override
	public final boolean isAdmin() { return false; }
	@Override
	public final String getInvoke() { return "ud"; }
	@Override
	public final Category getCategory() { return Category.MISC; }
	@Override
	public final String getUsage() { return "s!ud <phrase>"; }

}