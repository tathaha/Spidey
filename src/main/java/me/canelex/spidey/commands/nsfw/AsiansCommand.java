package me.canelex.spidey.commands.nsfw;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class AsiansCommand extends Command
{
    public AsiansCommand()
    {
        super("asiansgonewild", new String[]{"asian", "asians"}, "Sends a (NSFW) picture of an asian", "asiansgonewild", Category.NSFW,
                Permission.UNKNOWN, 0, 4);
    }

    @Override
    public void execute(final String[] args, final Message msg) {}
}
