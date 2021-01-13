package dev.mlnr.spidey.commands.fun;

import dev.mlnr.spidey.cache.AkinatorCache;
import dev.mlnr.spidey.objects.akinator.AkinatorContext;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

public class AkinatorCommand extends Command
{
    public AkinatorCommand()
    {
        super("akinator", new String[]{"aki"}, Category.FUN, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        AkinatorCache.createAkinator(ctx.getAuthor(), new AkinatorContext(ctx.getEvent()));
    }
}