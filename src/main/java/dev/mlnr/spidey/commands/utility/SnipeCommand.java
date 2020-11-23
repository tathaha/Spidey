package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.Core;
import dev.mlnr.spidey.cache.MessageCache;
import dev.mlnr.spidey.cache.settings.GuildSettingsCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

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
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var guildId = ctx.getGuild().getIdLong();
        if (!GuildSettingsCache.isSnipingEnabled(guildId))
        {
            ctx.replyError("Sniping messages for this server is disabled, which could be caused by manually disabling it or having more than 10000 people in this server. You can enable sniping by using `"
                    + GuildSettingsCache.getPrefix(guildId) + "sniping`");
            return;
        }
        final var textChannel = ctx.getTextChannel();
        final var channelId = textChannel.getIdLong();
        final var lastDeletedMessage = MessageCache.getLastDeletedMessage(channelId);
        if (lastDeletedMessage == null)
        {
            ctx.replyError("There's no deleted message to snipe");
            return;
        }
        final var eb = Utils.createEmbedBuilder(ctx.getAuthor());
        eb.setTimestamp(lastDeletedMessage.getCreation());
        eb.setDescription(lastDeletedMessage.getContent());
        eb.setColor(Color.GREEN);

        ctx.getJDA().retrieveUserById(lastDeletedMessage.getAuthorId()).queue(user ->
        {
            eb.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
            ctx.reply(eb);
            Core.getScheduler().schedule(() -> MessageCache.uncacheMessage(lastDeletedMessage.getChannelId(), lastDeletedMessage.getId()), 2, TimeUnit.MINUTES);
        });
    }
}