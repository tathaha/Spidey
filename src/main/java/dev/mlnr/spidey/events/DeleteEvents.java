package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DeleteEvents extends ListenerAdapter {
	private final Cache cache;

	public DeleteEvents(Cache cache) {
		this.cache = cache;
	}

	@Override
	public void onTextChannelDelete(TextChannelDeleteEvent event) {
		var guildSettingsCache = cache.getGuildSettingsCache();
		var guildId = event.getGuild().getIdLong();
		var channel = event.getChannel();

		var miscSettings = guildSettingsCache.getMiscSettings(guildId);
		var channelsSettings = guildSettingsCache.getChannelsSettings(guildId);
		if (channel.getIdLong() == miscSettings.getLogChannelId()) {
			miscSettings.removeLogChannel();
		}
		if (channelsSettings.isChannelWhitelisted(channel, true)) {
			channelsSettings.removeWhitelistedChannel(channel);
		}
		else if (channelsSettings.isChannelBlacklisted(channel)) {
			channelsSettings.removeBlacklistedChannel(channel);
		}
	}

	@Override
	public void onRoleDelete(RoleDeleteEvent event) {
		var roleId = event.getRole().getIdLong();
		var guildId = event.getGuild().getIdLong();
		var guildSettingsCache = cache.getGuildSettingsCache();
		var miscSettings = guildSettingsCache.getMiscSettings(guildId);
		var musicSettings = guildSettingsCache.getMusicSettings(guildId);
		var filterSettings = guildSettingsCache.getFiltersSettings(guildId);
		if (roleId == miscSettings.getJoinRoleId()) {
			miscSettings.removeJoinRole();
		}
		if (roleId == musicSettings.getDJRoleId()) {
			musicSettings.removeDJRole();
		}
		if (filterSettings.isRoleIgnored(roleId)) {
			filterSettings.removeIgnoredRole(roleId);
		}
	}
}