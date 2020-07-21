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
        super("snipe", new String[]{"s", "dsnipe"}, "Snipes a deleted message", "snipe", Category.UTILITY, Permission.UNKNOWN, 0, 6);
    }

    @Override
    public void execute(final String[] args, final Message msg)
    {
        final var textChannel = msg.getTextChannel();
        final var channelId = textChannel.getIdLong();
        final var lastDeletedMessage = Cache.getLastDeletedMessage(channelId);
        if (lastDeletedMessage == null)
        {
            Utils.returnError("There's no deleted message to snipe", msg);
            return;
        }
        final var eb = Utils.createEmbedBuilder(msg.getAuthor());
        eb.setTimestamp(lastDeletedMessage.getCreation());
        eb.setDescription(lastDeletedMessage.getContent());
        eb.setColor(Color.GREEN);
        msg.getJDA().retrieveUserById(lastDeletedMessage.getAuthorId()).queue(user -> eb.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl()));

        Utils.sendMessage(textChannel, eb.build());
        Core.getExecutor().schedule(() -> Cache.uncacheMessage(lastDeletedMessage.getChannelId(), lastDeletedMessage.getId()), 2, TimeUnit.MINUTES);
    }
}