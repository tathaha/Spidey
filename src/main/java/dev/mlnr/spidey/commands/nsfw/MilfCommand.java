package dev.mlnr.spidey.commands.nsfw;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class MilfCommand extends Command
{
    public MilfCommand()
    {
        super("milf", new String[]{"milfs"}, "Sends a (NSFW) picture of a milf", "milf", Category.NSFW, Permission.UNKNOWN, 0, 4);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx) {}
}