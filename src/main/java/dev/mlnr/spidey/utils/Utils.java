package dev.mlnr.spidey.utils;

import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.cache.GeneralCache;
import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.cache.ResponseCache;
import dev.mlnr.spidey.objects.guild.InviteData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Utils
{
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EE, d.LLL y | HH:mm:ss");
    public static final Pattern TEXT_PATTERN = Pattern.compile("[a-zA-Z0-9-_]+");
    public static final int SPIDEY_COLOR = 3288807;

    private Utils() {}

    public static void sendMessage(TextChannel channel, MessageEmbed embed)
    {
        sendMessage(channel, embed, null);
    }

    public static void sendMessage(TextChannel channel, MessageEmbed embed, Message invokeMessage)
    {
        if (channel.canTalk() && channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS))
            channel.sendMessage(embed).queue(response -> setResponse(invokeMessage, response));
    }

    public static void sendMessage(TextChannel channel, String toSend)
    {
        sendMessage(channel, toSend, MessageAction.getDefaultMentions(), null);
    }

    public static void sendMessage(TextChannel channel, String toSend, Set<Message.MentionType> allowedMentions, Message invokeMessage)
    {
        if (channel.canTalk())
        {
            channel.sendMessage(toSend).allowedMentions(allowedMentions == null ? EnumSet.noneOf(Message.MentionType.class) : allowedMentions) // passing null to allowedMentions allows all mentions, nice logic JDA
                    .queue(response -> setResponse(invokeMessage, response));
        }
    }

    private static void setResponse(Message invokeMessage, Message responseMessage)
    {
        if (invokeMessage != null)
            ResponseCache.setResponseMessageId(invokeMessage.getIdLong(), responseMessage.getIdLong());
    }

    public static void deleteMessage(Message msg)
    {
        var channel = msg.getTextChannel();
        if (channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE))
            msg.delete().queue();
    }

    public static EmbedBuilder createEmbedBuilder(User user)
    {
        return new EmbedBuilder().setFooter("Command executed by " + user.getAsTag(), user.getEffectiveAvatarUrl()).setColor(0xFEFEFE);
    }

    public static void addReaction(Message message, String reaction)
    {
        if (message.getGuild().getSelfMember().hasPermission(message.getTextChannel(), Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION))
            message.addReaction(reaction).queue();
    }

    public static void returnError(String errMsg, Message origin)
    {
        returnError(errMsg, origin, Emojis.CROSS);
    }

    public static void returnError(String errMsg, Message origin, String failureEmoji)
    {
        addReaction(origin, failureEmoji);
        var channel = origin.getTextChannel();
        if (!channel.canTalk())
            return;
        channel.sendMessage(":no_entry: " + errMsg).queue(errorMessage ->
        {
            setResponse(origin, errorMessage);
            var guild = channel.getGuild();
            if (!guild.getSelfMember().hasPermission(Permission.MESSAGE_MANAGE) || !GuildSettingsCache.isErrorCleanupEnabled(guild.getIdLong()))
                return;
            Spidey.getScheduler().schedule(() -> channel.purgeMessages(origin, errorMessage), 10, TimeUnit.SECONDS);
        });
    }

    public static void storeInvites(Guild guild)
    {
        if (guild.getSelfMember().hasPermission(Permission.MANAGE_SERVER))
            guild.retrieveInvites().queue(invites -> invites.forEach(invite -> GeneralCache.getInviteCache().put(invite.getCode(), new InviteData(invite))));
    }

    public static String formatDate(OffsetDateTime date)
    {
        return DATE_FORMATTER.format(date);
    }

    public static int getColorHex(int value, int max)
    {
        var r = ((255 * value) / max);
        var g = (255 * (max - value)) / max;
        return ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8);
    }

    public static <K, V> ExpiringMap<K, V> createDefaultExpiringMap()
    {
        return ExpiringMap.builder()
                .expirationPolicy(ExpirationPolicy.ACCESSED)
                .expiration(2, TimeUnit.MINUTES)
                .build();
    }
}