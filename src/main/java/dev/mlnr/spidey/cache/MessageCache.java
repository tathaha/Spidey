package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.objects.messages.MessageData;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static dev.mlnr.spidey.utils.Utils.createDefaultExpiringMap;

public class MessageCache {
	private final Map<Long, MessageData> messageDataMap = ExpiringMap.builder()
			.maxSize(10000)
			.expirationPolicy(ExpirationPolicy.CREATED)
			.expiration(10, TimeUnit.MINUTES)
			.build();
	private final Map<Long, Long> lastDeletedMessageMap = createDefaultExpiringMap();

	private final Map<Long, Long> lastEditedMessageMap = createDefaultExpiringMap();
	private final Map<Long, MessageData> lastEditedMessageDataMap = createDefaultExpiringMap();

	public MessageData getLastDeletedMessage(long channelId) {
		var latest = lastDeletedMessageMap.get(channelId);
		return latest == null ? null : messageDataMap.get(latest);
	}

	public void setLastDeletedMessage(long channelId, long messageId) {
		lastDeletedMessageMap.put(channelId, messageId);
	}

	public void cacheMessage(long messageId, MessageData message) {
		var data = messageDataMap.get(messageId);
		if (data != null) {
			lastEditedMessageDataMap.put(messageId, data);
		}
		messageDataMap.put(messageId, message);
	}

	public boolean isCached(long messageId) {
		return messageDataMap.containsKey(messageId);
	}

	// MESSAGE EDITING CACHING

	public void setLastEditedMessage(long channelId, long messageId) {
		lastEditedMessageMap.put(channelId, messageId);
	}

	public MessageData getLastEditedMessage(long channelId) {
		var latest = lastEditedMessageMap.get(channelId);
		return latest == null ? null : lastEditedMessageDataMap.get(latest);
	}

	// other

	public void pruneCache(long guildId) {
		var it = messageDataMap.entrySet().iterator(); // using iterator here instead of a for loop should prevent getting CMEs
		while (it.hasNext()) {
			var next = it.next();
			var dataGuildId = next.getValue().getGuildId();
			if (dataGuildId != guildId) {
				continue;
			}
			var messageId = next.getKey();
			it.remove();
			lastDeletedMessageMap.entrySet().removeIf(entry1 -> entry1.getValue() == messageId);
			lastEditedMessageMap.entrySet().removeIf(entry1 -> entry1.getValue() == messageId);
			lastEditedMessageDataMap.entrySet().removeIf(entry1 -> entry1.getValue().getGuildId() == guildId);
		}
	}
}