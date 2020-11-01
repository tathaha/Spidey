package dev.mlnr.spidey.commands.fun;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class DankMemesCommand extends Command
{
    public DankMemesCommand()
    {
        super("dankmemes", new String[]{"dankmeme", "dank"}, "Sends a random dank meme", "dankmemes", Category.FUN, Permission.UNKNOWN, 0, 4);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx) {}
}