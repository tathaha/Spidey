package dev.mlnr.spidey.commands.nsfw;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class BlowjobsCommand extends Command
{
    public BlowjobsCommand()
    {
        super("blowjobs", new String[]{"blowjob"}, Category.NSFW, Permission.UNKNOWN, 0, 4);
    }

    @Override
    public void execute(String[] args, CommandContext ctx) {}
}