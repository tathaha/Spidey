package dev.mlnr.spidey.commands.nsfw;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class LesbiansCommand extends Command {

	public LesbiansCommand() {
		super("lesbians", new String[]{"lesbian"}, Category.NSFW, Permission.UNKNOWN, 0, 4);
	}

	@Override
	public boolean execute(String[] args, CommandContext ctx) {
		return true;
	}
}