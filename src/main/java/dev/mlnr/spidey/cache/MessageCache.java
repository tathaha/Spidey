package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.objects.messages.MessageData;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static dev.mlnr.spidey.utils.Utils.createDefaultExpiringMap;

public class MessageCache {
	private final Map<Long, MessageData> deletedMessagesData = ExpiringMap.builder() // message id -> message data
			.maxSize(10000)
			.expirationPolicy(ExpirationPolicy.CREATED)
			.expiration(10, TimeUnit.MINUTES)
			.build();
	private final Map<Long, Long> lastDeletedInChannelMap = createDefaultExpiringMap(); // channel id -> message id

	private final Map<Long, Long> lastEditedInChannelMap = createDefaultExpiringMap(); // channel id -> message id
	private final Map<Long, MessageData> editedMessagesData = createDefaultExpiringMap(); // message id -> message data

	public MessageData getLastDeletedMessage(long channelId) {
		var latest = lastDeletedInChannelMap.get(channelId);
		return latest == null ? null : deletedMessagesData.get(latest);
	}

	public void setLastDeletedMessage(long channelId, long messageId) {
		lastDeletedInChannelMap.put(channelId, messageId);
	}

	public void cacheMessage(long messageId, MessageData message) {
		var data = deletedMessagesData.get(messageId);
		if (data != null) {
			editedMessagesData.put(messageId, data);
		}
		deletedMessagesData.put(messageId, message);
	}

	public boolean isCached(long messageId) {
		return deletedMessagesData.containsKey(messageId);
	}

	// MESSAGE EDITING CACHING

	public void setLastEditedMessage(long channelId, long messageId) {
		lastEditedInChannelMap.put(channelId, messageId);
	}

	public MessageData getLastEditedMessage(long channelId) {
		var latest = lastEditedInChannelMap.get(channelId);
		return latest == null ? null : editedMessagesData.get(latest);
	}

	// other

	public void pruneCache(long guildId) {
		var it = deletedMessagesData.entrySet().iterator(); // using iterator here instead of a for loop should prevent getting CMEs
		while (it.hasNext()) {
			var next = it.next();
			var dataGuildId = next.getValue().getGuildId();
			if (dataGuildId != guildId) {
				continue;
			}
			var messageId = next.getKey();
			it.remove();
			lastDeletedInChannelMap.entrySet().removeIf(entry1 -> entry1.getValue() == messageId);
			lastEditedInChannelMap.entrySet().removeIf(entry1 -> entry1.getValue() == messageId);
			editedMessagesData.entrySet().removeIf(entry1 -> entry1.getValue().getGuildId() == guildId);
		}
	}

	public void pruneCacheForChannel(long channelId) {
		var lastDeletedMessageInChannel = lastDeletedInChannelMap.get(channelId);
		var lastEditedMessageInChannel = lastEditedInChannelMap.get(channelId);
		if (lastDeletedMessageInChannel != null || lastEditedMessageInChannel != null) { // if the channel has deleted or edited data, remove it
			deletedMessagesData.remove(lastDeletedMessageInChannel);
			editedMessagesData.remove(lastDeletedMessageInChannel);
		}
		lastDeletedInChannelMap.remove(channelId);
		lastEditedInChannelMap.remove(channelId);
	}
}