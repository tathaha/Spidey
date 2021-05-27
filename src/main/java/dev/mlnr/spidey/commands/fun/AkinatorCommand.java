package dev.mlnr.spidey.commands.fun;

import dev.mlnr.spidey.objects.akinator.AkinatorContext;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class AkinatorCommand extends Command {
	public AkinatorCommand() {
		super("akinator", Category.FUN, Permission.UNKNOWN, 0);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var akinatorCache = ctx.getCache().getAkinatorCache();
		akinatorCache.createAkinator(ctx.getUser(), new AkinatorContext(ctx.getEvent(), akinatorCache, ctx.getI18n()));
		return true;
	}
}