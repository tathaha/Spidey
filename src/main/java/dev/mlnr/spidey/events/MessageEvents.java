package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.handlers.akinator.AkinatorHandler;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.objects.akinator.AkinatorContext;
import dev.mlnr.spidey.objects.messages.MessageData;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Map;
import java.util.stream.Collectors;

public class MessageEvents extends ListenerAdapter {
	private final Cache cache;

	public MessageEvents(Cache cache) {
		this.cache = cache;
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.isWebhookMessage()) {
			return;
		}
		var guild = event.getGuild();
		var message = event.getMessage();
		var content = message.getContentRaw().trim();
		var guildSettingsCache = cache.getGuildSettingsCache();
		var guildId = guild.getIdLong();
		var filtersSettings = guildSettingsCache.getFiltersSettings(guildId);

		if (message.getType() == MessageType.CHANNEL_PINNED_ADD) {
			if (filtersSettings.isPinnedDeletingEnabled())
				Utils.deleteMessage(message);
			return;
		}

		if (content.isEmpty()) {
			return;
		}

		var miscSettings = guildSettingsCache.getMiscSettings(guildId);

		if (miscSettings.isSnipingEnabled()) {
			cache.getMessageCache().cacheMessage(message.getIdLong(), new MessageData(message));
		}

		var member = event.getMember();
		var channel = event.getChannel();
		if (filtersSettings.isInviteDeletingEnabled() && !filtersSettings.isIgnored(member) && guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE)
				&& !member.hasPermission(Permission.MESSAGE_MANAGE)) {
			var invitesForGuild = cache.getGeneralCache().getInviteCache().entrySet().stream().filter(entry -> entry.getValue().getGuildId() == guildId).map(Map.Entry::getKey).collect(Collectors.toList());
			if (!invitesForGuild.isEmpty() && message.getInvites().stream().anyMatch(code -> !invitesForGuild.contains(code))) {
				Utils.deleteMessage(message);
				return;
			}
		}

		var author = event.getAuthor();
		var akinatorCache = cache.getAkinatorCache();
		var akinatorData = akinatorCache.getAkinatorData(author.getIdLong());
		if (akinatorData != null && akinatorData.getChannelId() == channel.getIdLong()) {
			AkinatorHandler.handle(author, new AkinatorContext(event, akinatorCache, miscSettings.getI18n()));
			return;
		}
	}

	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
		var messageId = event.getMessageIdLong();
		var channel = event.getChannel();
		var paginatorCache = cache.getPaginatorCache();

		if (paginatorCache.isPaginator(messageId)) {
			paginatorCache.removePaginator(messageId);
		}
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