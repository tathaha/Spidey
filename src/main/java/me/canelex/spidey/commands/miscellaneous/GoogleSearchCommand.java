package me.canelex.spidey.commands.miscellaneous;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Message;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.objects.search.GoogleSearch;
import me.canelex.spidey.utils.Utils;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unused")
public class GoogleSearchCommand extends Command
{
	public GoogleSearchCommand()
	{
		super("g", new String[]{}, "Allows you to search for results on Google", "g <query>", Category.MISC, Permission.UNKNOWN, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var result = new GoogleSearch().getResult(StringUtils.join(args, "+", 1, args.length));
		Utils.sendMessage(message.getChannel(), result.getContent());
	}
}