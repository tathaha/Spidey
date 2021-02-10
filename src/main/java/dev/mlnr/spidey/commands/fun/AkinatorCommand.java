package dev.mlnr.spidey.commands.fun;

import dev.mlnr.spidey.objects.akinator.AkinatorContext;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class AkinatorCommand extends Command {

	public AkinatorCommand() {
		super("akinator", new String[]{"aki"}, Category.FUN, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var akinatorCache = ctx.getCache().getAkinatorCache();
		var guildId = ctx.getGuild().getIdLong();
		akinatorCache.createAkinator(ctx.getAuthor(), new AkinatorContext(ctx.getEvent(), akinatorCache, ctx.getCache().getGuildSettingsCache().getMiscSettings(guildId).getI18n()));
	}
}