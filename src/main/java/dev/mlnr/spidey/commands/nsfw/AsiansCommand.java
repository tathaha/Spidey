package dev.mlnr.spidey.commands.nsfw;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class AsiansCommand extends Command {

	public AsiansCommand() {
		super("asiansgonewild", new String[]{"asian", "asians"}, Category.NSFW, Permission.UNKNOWN, 0, 4);
	}

	@Override
	public boolean execute(String[] args, CommandContext ctx) {
		return true;
	}
}