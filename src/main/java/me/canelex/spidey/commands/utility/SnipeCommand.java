package me.canelex.spidey.commands.utility;

import me.canelex.spidey.Core;
import me.canelex.spidey.objects.cache.Cache;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class SnipeCommand extends Command
{
    public SnipeCommand()
    {
        super("snipe", new String[]{}, "Snipes a deleted message", "snipe", Category.UTILITY, Permission.UNKNOWN, 0, 6);
    }

    @Override
    public void execute(final String[] args, final Message msg)
    {
        final var lastMessage = Cache.getLastMessageDeleted();
        if (lastMessage == null)
        {
            Utils.returnError("There's nothing to snipe", msg);
            return;
        }
        final var eb = Utils.createEmbedBuilder(msg.getAuthor());
        eb.setTimestamp(lastMessage.getCreation());
        eb.setDescription(lastMessage.getContent());
        eb.setColor(Color.GREEN);
        msg.getJDA().retrieveUserById(lastMessage.getAuthorId()).queue(user -> eb.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl()));

        Utils.sendMessage(msg.getTextChannel(), eb.build());
        Core.getExecutor().schedule(() -> Cache.uncacheMessage(lastMessage.getId()), 2, TimeUnit.MINUTES);
    }
}