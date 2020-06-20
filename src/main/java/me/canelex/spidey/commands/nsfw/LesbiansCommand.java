package me.canelex.spidey.commands.nsfw;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class LesbiansCommand extends Command
{
    public LesbiansCommand()
    {
        super("lesbians", new String[]{"lesbian"}, "Sends a (NSFW) picture of lesbians", "lesbians", Category.NSFW, Permission.UNKNOWN,
                0, 4);
    }

    @Override
    public void execute(final String[] args, final Message msg) {}
}
