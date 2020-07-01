package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.Core;
import dev.mlnr.spidey.objects.cache.Cache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
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
        final var textChannel = msg.getTextChannel();
        final var channelId = textChannel.getIdLong();
        final var lastMessage = Cache.getLastMessageDeleted(channelId);
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

        Utils.sendMessage(textChannel, eb.build());
        Core.getExecutor().schedule(() -> Cache.uncacheMessage(lastMessage.getChannelId(), lastMessage.getId()), 2, TimeUnit.MINUTES);
    }
}