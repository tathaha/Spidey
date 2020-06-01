package me.canelex.spidey.commands.social;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.objects.search.GoogleSearch;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unused")
public class YouTubeSearchCommand extends Command
{
    public YouTubeSearchCommand()
    {
        super("yt", new String[]{}, "Allows you to search for results on YouTube", "yt <query>", Category.SOCIAL,
                Permission.UNKNOWN, 0);
    }

    @Override
    public final void execute(final String[] args, final Message message)
    {
        final var result = new GoogleSearch().getResult(StringUtils.join(args, "+", 0, args.length) + "+site:youtube.com");
        Utils.sendMessage(message.getChannel(), result.getContent());
    }
}