package me.canelex.spidey.commands.miscellaneous;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.objects.json.UrbanDictionary;
import me.canelex.spidey.utils.Emojis;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class UrbanDictionaryCommand extends Command
{
	public UrbanDictionaryCommand()
	{
		super("ud", new String[]{}, "Returns the definition(s) of a phrase", "ud <phrase>", Category.MISC, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		if (args.length == 0)
		{
			Utils.returnError("Please specify a term", message);
			return;
		}
		final var term = args[0];
		final var channel = message.getTextChannel();
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