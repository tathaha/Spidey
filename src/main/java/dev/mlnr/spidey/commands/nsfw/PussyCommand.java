package dev.mlnr.spidey.commands.nsfw;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class PussyCommand extends Command {

	public PussyCommand() {
		super("pussy", new String[]{"vagina"}, Category.NSFW, Permission.UNKNOWN, 0, 4);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {}
}