package dev.mlnr.spidey.commands.fun;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class FunnyCommand extends Command
{
    public FunnyCommand()
    {
        super("funny", new String[]{}, "Sends a random funny picture", "funny", Category.FUN, Permission.UNKNOWN, 0, 4);
    }

    @Override
    public void execute(final String[] args, final Message msg) {}
}