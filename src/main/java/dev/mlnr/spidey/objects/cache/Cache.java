package dev.mlnr.spidey.objects.cache;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.invites.InviteData;
import dev.mlnr.spidey.objects.messages.MessageData;
import dev.mlnr.spidey.utils.collections.CollectionUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Cache
{
    private static final Map<Long, String> PREFIX_CACHE = new HashMap<>();
    private static final Map<String, InviteData> INVITE_CACHE = new HashMap<>();
    private static final Map<Long, Long> LOG_CHANNEL_CACHE = new HashMap<>();
    private static final Map<Long, Long> JOIN_ROLE_CACHE = new HashMap<>();
    private static final Map<Long, Boolean> VIP_GUILDS_CACHE = new HashMap<>();
    private static final Map<Long, List<String>> REDDIT_CACHE = new HashMap<>();

    private static final Map<Long, MessageData> MESSAGE_CACHE = new ConcurrentHashMap<>();
    private static final Map<Long, Long> LAST_MESSAGE_DELETED_CACHE = new HashMap<>();

    private static final Map<Long, Long> LAST_MESSAGE_EDITED_CACHE = new HashMap<>();
    private static final Map<Long, MessageData> LAST_MESSAGE_EDITED_DATA = new HashMap<>();

    private Cache()
    {
        super();
    }

    public static Map<String, InviteData> getInviteCache()
    {
        return INVITE_CACHE;
    }

    // PREFIX CACHING

    public static String retrievePrefix(final long guildId)
    {
        return Objects.requireNonNullElseGet(PREFIX_CACHE.get(guildId), () ->
        {
            final var retrieved = DatabaseManager.retrievePrefix(guildId);
            final var prefix = retrieved.isEmpty() ? "s!" : retrieved;
            PREFIX_CACHE.put(guildId, prefix);
            return prefix;
        });
    }

    public static void setPrefix(final long guildId, final String prefix)
    {
        DatabaseManager.setPrefix(guildId, prefix);
        PREFIX_CACHE.put(guildId, prefix);
    }

    // LOG CHANNEL CACHING

    public static long retrieveLogChannel(final long guildId)
    {
        return Objects.requireNonNullElseGet(LOG_CHANNEL_CACHE.get(guildId), () ->
        {
            final var channel = DatabaseManager.retrieveChannel(guildId);
            LOG_CHANNEL_CACHE.put(guildId, channel);
            return channel;
        });
    }

    public static TextChannel getLogAsChannel(final long guildId, final JDA jda)
    {
        return jda.getTextChannelById(retrieveLogChannel(guildId));
    }

    public static void setLogChannel(final long guildId, final long channelId)
    {
        LOG_CHANNEL_CACHE.put(guildId, channelId);
        DatabaseManager.setChannel(guildId, channelId);
    }

    public static void removeLogChannel(final long guildId)
    {
        setLogChannel(guildId, 0);
    }

    // JOIN ROLE CACHING

    public static long retrieveJoinRole(final long guildId)
    {
        return Objects.requireNonNullElseGet(JOIN_ROLE_CACHE.get(guildId), () ->
        {
            final var role = DatabaseManager.retrieveRole(guildId);
            JOIN_ROLE_CACHE.put(guildId, role);
            return role;
        });
    }

    public static Role getJoinRole(final long guildId, final JDA jda)
    {
        return jda.getRoleById(retrieveJoinRole(guildId));
    }

    public static void setJoinRole(final long guildId, final long roleId)
    {
        JOIN_ROLE_CACHE.put(guildId, roleId);
        DatabaseManager.setRole(guildId, roleId);
    }

    public static void removeJoinRole(final long guildId)
    {
        setJoinRole(guildId, 0);
    }

    // VIP GUILDS CACHING

    public static boolean isVip(final long guildId)
    {
        return Objects.requireNonNullElseGet(VIP_GUILDS_CACHE.get(guildId), () ->
        {
            final var vip = DatabaseManager.isVip(guildId);
            VIP_GUILDS_CACHE.put(guildId, vip);
            return vip;
        });
    }

    // REDDIT POSTS CACHING

    public static boolean isPostCached(final long guildId, final String json)
    {
        return CollectionUtils.contains(REDDIT_CACHE, guildId, json);
    }

    public static void cachePost(final long guildId, final String json)
    {
        CollectionUtils.add(REDDIT_CACHE, guildId, json);
    }

    // MESSAGE CACHING

    public static MessageData getLastDeletedMessage(final long channelId)
    {
        return MESSAGE_CACHE.get(LAST_MESSAGE_DELETED_CACHE.get(channelId));
    }

    public static void setLastDeletedMessage(final long channelId, final long messageId)
    {
        LAST_MESSAGE_DELETED_CACHE.put(channelId, messageId);
    }

    public static void cacheMessage(final long messageId, final MessageData message)
    {
        if (MESSAGE_CACHE.containsKey(messageId))
            LAST_MESSAGE_EDITED_DATA.put(messageId, MESSAGE_CACHE.get(messageId));
        MESSAGE_CACHE.put(messageId, message);
    }

    public static void uncacheMessage(final long channelId, final long messageId)
    {
        MESSAGE_CACHE.remove(messageId);
        LAST_MESSAGE_DELETED_CACHE.remove(channelId);
        LAST_MESSAGE_EDITED_DATA.remove(messageId);
        LAST_MESSAGE_EDITED_CACHE.remove(channelId, messageId);
    }

    public static Map<Long, MessageData> getMessageCache()
    {
        return MESSAGE_CACHE;
    }

    // MESSAGE EDITING CACHING

    public static void setLastEditedMessage(final long channelId, final long messageId)
    {
        LAST_MESSAGE_EDITED_CACHE.put(channelId, messageId);
    }

    public static MessageData getLastEditedMessage(final long channelId)
    {
        return LAST_MESSAGE_EDITED_DATA.get(LAST_MESSAGE_EDITED_CACHE.get(channelId));
    }

    public static void uncacheEditedMessage(final long channelId, final long messageId)
    {
        LAST_MESSAGE_EDITED_CACHE.remove(channelId, messageId);
        LAST_MESSAGE_EDITED_DATA.remove(messageId);
    }

    // MISC

    public static void removeEntry(final long guildId)
    {
        if (isVip(guildId))
            return;
        LOG_CHANNEL_CACHE.remove(guildId);
        JOIN_ROLE_CACHE.remove(guildId);
        PREFIX_CACHE.remove(guildId);
        DatabaseManager.removeEntry(guildId);
    }

    public static int getCooldown(final long guildId, final Command cmd)
    {
        final var cooldown = cmd.getCooldown();
        if (isVip(guildId))
            return cooldown / 2;
        return cooldown;
    }
}
