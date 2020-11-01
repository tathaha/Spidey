package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.objects.messages.MessageData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageCache
{
    private static final Map<Long, MessageData> MESSAGE_CACHE = new ConcurrentHashMap<>();
    private static final Map<Long, Long> LAST_MESSAGE_DELETED_CACHE = new HashMap<>();

    private static final Map<Long, Long> LAST_MESSAGE_EDITED_CACHE = new HashMap<>();
    private static final Map<Long, MessageData> LAST_MESSAGE_EDITED_DATA = new HashMap<>();

    private MessageCache() {}

    public static MessageData getLastDeletedMessage(final long channelId)
    {
        final var latest = LAST_MESSAGE_DELETED_CACHE.get(channelId);
        return latest == null ? null : MESSAGE_CACHE.get(latest);
    }

    public static void setLastDeletedMessage(final long channelId, final long messageId)
    {
        LAST_MESSAGE_DELETED_CACHE.put(channelId, messageId);
    }

    public static void cacheMessage(final long messageId, final MessageData message)
    {
        final var data = MESSAGE_CACHE.get(messageId);
        if (data != null)
            LAST_MESSAGE_EDITED_DATA.put(messageId, data);
        MESSAGE_CACHE.put(messageId, message);
    }

    public static void uncacheMessage(final long channelId, final long messageId)
    {
        MESSAGE_CACHE.remove(messageId);
        LAST_MESSAGE_DELETED_CACHE.remove(channelId);
        LAST_MESSAGE_EDITED_DATA.remove(messageId);
        LAST_MESSAGE_EDITED_CACHE.remove(channelId, messageId);
    }

    public static boolean isCached(final long messageId){
        return MESSAGE_CACHE.containsKey(messageId);
    }

    // MESSAGE EDITING CACHING

    public static void setLastEditedMessage(final long channelId, final long messageId)
    {
        LAST_MESSAGE_EDITED_CACHE.put(channelId, messageId);
    }

    public static MessageData getLastEditedMessage(final long channelId)
    {
        final var latest = LAST_MESSAGE_EDITED_CACHE.get(channelId);
        return latest == null ? null : LAST_MESSAGE_EDITED_DATA.get(latest);
    }

    public static void uncacheEditedMessage(final long channelId, final long messageId)
    {
        LAST_MESSAGE_EDITED_CACHE.remove(channelId, messageId);
        LAST_MESSAGE_EDITED_DATA.remove(messageId);
    }

    public static void pruneCache(final long guildId)
    {
        final var entries = MESSAGE_CACHE.entrySet();
        for (var entry : entries)
        {
            final var dataGuildId = entry.getValue().getGuildId();
            if (dataGuildId != guildId)
                continue;
            final var messageId = entry.getKey();
            MESSAGE_CACHE.remove(messageId);
            LAST_MESSAGE_DELETED_CACHE.entrySet().removeIf(record -> record.getValue() == messageId);
            LAST_MESSAGE_EDITED_CACHE.entrySet().removeIf(record -> record.getValue() == messageId);
            LAST_MESSAGE_EDITED_DATA.entrySet().removeIf(record -> record.getValue().getGuildId() == guildId);
        }
    }

    public static Map<Long, MessageData> getCache()
    {
        return MESSAGE_CACHE;
    }
}