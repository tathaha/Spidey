package dev.mlnr.spidey.commands.nsfw;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class AnalCommand extends Command
{
    public AnalCommand()
    {
        super("anal", new String[]{}, "Sends a (NSFW) picture of an anal", "anal", Category.NSFW, Permission.UNKNOWN, 0, 4);
    }

    @Override
    public void execute(final String[] args, final Message msg) {}
}
