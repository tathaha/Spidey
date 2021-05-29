package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.objects.messages.MessageData;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageEvents extends ListenerAdapter {
	private final Cache cache;

	public MessageEvents(Cache cache) {
		this.cache = cache;
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		var guild = event.getGuild();
		var message = event.getMessage();
		var content = message.getContentRaw().trim();
		var guildSettingsCache = cache.getGuildSettingsCache();
		var guildId = guild.getIdLong();

		if (content.isEmpty()) {
			return;
		}
		var miscSettings = guildSettingsCache.getMiscSettings(guildId);
		if (miscSettings.isSnipingEnabled()) {
			cache.getMessageCache().cacheMessage(message.getIdLong(), new MessageData(message));
		}
	}

	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
		var messageId = event.getMessageIdLong();
		var channel = event.getChannel();

		if (!cache.getGuildSettingsCache().getMiscSettings(event.getGuild().getIdLong()).isSnipingEnabled()) {
			return;
		}
		var messageCache = cache.getMessageCache();
		if (!messageCache.isCached(messageId)) {
			return;
		}
		messageCache.setLastDeletedMessage(channel.getIdLong(), messageId);
	}

	@Override
	public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
		if (!cache.getGuildSettingsCache().getMiscSettings(event.getGuild().getIdLong()).isSnipingEnabled()) {
			return;
		}
		var messageCache = cache.getMessageCache();
		var messageId = event.getMessageIdLong();
		if (!messageCache.isCached(messageId)) {
			return;
		}
		messageCache.cacheMessage(messageId, new MessageData(event.getMessage()));
		messageCache.setLastEditedMessage(event.getChannel().getIdLong(), messageId);
	}
}