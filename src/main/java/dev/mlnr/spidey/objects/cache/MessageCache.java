package dev.mlnr.spidey.objects.cache;

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

    private MessageCache()
    {
        super();
    }

    public static MessageData getLastDeletedMessage(final long channelId)
    {
        if (!LAST_MESSAGE_DELETED_CACHE.containsKey(channelId))
            return null;
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

    // MESSAGE EDITING CACHING

    public static void setLastEditedMessage(final long channelId, final long messageId)
    {
        LAST_MESSAGE_EDITED_CACHE.put(channelId, messageId);
    }

    public static MessageData getLastEditedMessage(final long channelId)
    {
        if (!LAST_MESSAGE_EDITED_CACHE.containsKey(channelId))
            return null;
        return LAST_MESSAGE_EDITED_DATA.get(LAST_MESSAGE_EDITED_CACHE.get(channelId));
    }

    public static void uncacheEditedMessage(final long channelId, final long messageId)
    {
        LAST_MESSAGE_EDITED_CACHE.remove(channelId, messageId);
        LAST_MESSAGE_EDITED_DATA.remove(messageId);
    }

    public static Map<Long, MessageData> getCache()
    {
        return MESSAGE_CACHE;
    }
}