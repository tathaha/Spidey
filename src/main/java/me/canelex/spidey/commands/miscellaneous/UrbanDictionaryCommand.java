package me.canelex.spidey.commands.miscellaneous;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Message;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.objects.json.UrbanDictionary;
import me.canelex.spidey.utils.Emojis;
import me.canelex.spidey.utils.Utils;

@SuppressWarnings("unused")
public class UrbanDictionaryCommand extends Command
{
	public UrbanDictionaryCommand()
	{
		super("ud", new String[]{}, "Returns the definition(s) of a phrase", "ud <phrase>", Category.MISC, Permission.UNKNOWN, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var term = message.getContentRaw().substring(5);
		final var channel = message.getChannel();
		final var ud = new UrbanDictionary(term);
		if (!ud.exists())
		{
			Utils.sendMessage(channel, ":no_entry: Query not found.");
			return;
		}
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
		Utils.sendMessage(channel, result);
	}
}