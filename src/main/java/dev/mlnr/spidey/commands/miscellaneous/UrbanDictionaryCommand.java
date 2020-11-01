package dev.mlnr.spidey.commands.miscellaneous;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.json.UrbanDictionary;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.Utils;
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
	public void execute(final String[] args, final Message msg)
	{
		if (args.length == 0)
		{
			Utils.returnError("Please specify a term", msg);
			return;
		}
		final var term = args[0];
		final var channel = msg.getTextChannel();
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