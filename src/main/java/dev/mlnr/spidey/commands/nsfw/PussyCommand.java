package dev.mlnr.spidey.commands.nsfw;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class PussyCommand extends Command
{
    public PussyCommand()
    {
        super("pussy", new String[]{"vagina"}, "Sends a (NSFW) picture of pussy", "pussy", Category.NSFW, Permission.UNKNOWN,
                0, 4);
    }

    @Override
    public void execute(final String[] args, final Message msg) {}
}
