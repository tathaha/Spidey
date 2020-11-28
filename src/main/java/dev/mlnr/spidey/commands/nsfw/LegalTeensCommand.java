package dev.mlnr.spidey.commands.nsfw;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class LegalTeensCommand extends Command
{
    public LegalTeensCommand()
    {
        super("legalteens", new String[]{"teens"}, "Sends a (NSFW) picture of a legal teen", "legalteens", Category.NSFW, Permission.UNKNOWN, 0, 4);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx) {}
}