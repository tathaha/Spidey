package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.Core;
import dev.mlnr.spidey.cache.MessageCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class EditSnipeCommand extends Command
{
    public EditSnipeCommand()
    {
        super("editsnipe", new String[]{"esnipe", "es"}, "Snipes an edited message", "editsnipe", Category.UTILITY, Permission.UNKNOWN, 0, 6);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var textChannel = ctx.getTextChannel();
        final var channelId = textChannel.getIdLong();
        final var lastEditedMessage = MessageCache.getLastEditedMessage(channelId);
        if (lastEditedMessage == null)
        {
            ctx.replyError("There's no edited message to snipe");
            return;
        }
        final var eb = Utils.createEmbedBuilder(ctx.getAuthor());
        eb.setTimestamp(lastEditedMessage.getCreation());
        eb.setDescription(lastEditedMessage.getContent());
        eb.setColor(Color.GREEN);

        ctx.getJDA().retrieveUserById(lastEditedMessage.getAuthorId()).queue(user ->
        {
            eb.setAuthor(user.getName(), lastEditedMessage.getJumpUrl(), user.getEffectiveAvatarUrl());
            ctx.reply(eb);
            Core.getExecutor().schedule(() -> MessageCache.uncacheEditedMessage(lastEditedMessage.getChannelId(), lastEditedMessage.getId()), 2, TimeUnit.MINUTES);
        });
    }
}