package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.CommandBase;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class LegalCommand extends CommandBase {
	public LegalCommand() {
		super("legal", "Shows you the Privacy Policy", Category.INFORMATIVE, Permission.UNKNOWN, 0);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		ctx.replyLocalized("commands.legal.text");
		return true;
	}
}