package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.cache.MessageCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.awt.*;

@SuppressWarnings("unused")
public class SnipeCommand extends Command
{
    public SnipeCommand()
    {
        super("snipe", new String[]{"s", "dsnipe"}, Category.UTILITY, Permission.UNKNOWN, 0, 6);
    }

    @Override
    public void execute(String[] args, CommandContext ctx)
    {
        var guildId = ctx.getGuild().getIdLong();
        var i18n = ctx.getI18n();
        if (!GuildSettingsCache.isSnipingEnabled(guildId))
        {
            ctx.replyError(i18n.get("sniping.disabled", GuildSettingsCache.getPrefix(guildId)));
            return;
        }
        var textChannel = ctx.getTextChannel();
        var channelId = textChannel.getIdLong();
        var lastDeletedMessage = MessageCache.getLastDeletedMessage(channelId);
        if (lastDeletedMessage == null)
        {
            ctx.replyError(i18n.get("sniping.no_message", "deleted"));
            return;
        }
        var eb = Utils.createEmbedBuilder(ctx.getAuthor());
        eb.setTimestamp(lastDeletedMessage.getCreation());
        eb.setDescription(lastDeletedMessage.getContent());
        eb.setColor(Color.GREEN);

        ctx.getJDA().retrieveUserById(lastDeletedMessage.getAuthorId()).queue(user ->
        {
            eb.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
            ctx.reply(eb);
        });
    }
}