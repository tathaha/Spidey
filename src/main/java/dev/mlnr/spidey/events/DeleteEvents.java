package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DeleteEvents extends ListenerAdapter {
	private final Cache cache;

	public DeleteEvents(Cache cache) {
		this.cache = cache;
	}

	@Override
	public void onChannelDelete(ChannelDeleteEvent event) {
		if (!event.isFromGuild()) {
			return;
		}
		var guildSettingsCache = cache.getGuildSettingsCache();
		var guildId = event.getGuild().getIdLong();
		var channel = event.getChannel();
		var channelId = channel.getIdLong();

		var miscSettings = guildSettingsCache.getMiscSettings(guildId);
		if (channelId == miscSettings.getLogChannelId()) {
			miscSettings.removeLogChannel();
		}
		cache.getMessageCache().pruneCacheForChannel(channelId);
	}

	@Override
	public void onRoleDelete(RoleDeleteEvent event) {
		var roleId = event.getRole().getIdLong();
		var guildId = event.getGuild().getIdLong();
		var guildSettingsCache = cache.getGuildSettingsCache();
		var miscSettings = guildSettingsCache.getMiscSettings(guildId);
		var musicSettings = guildSettingsCache.getMusicSettings(guildId);
		if (roleId == miscSettings.getJoinRoleId()) {
			miscSettings.removeJoinRole();
		}
		if (roleId == musicSettings.getDJRoleId()) {
			musicSettings.removeDJRole();
		}
	}
}