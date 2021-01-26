package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.objects.guild.InviteData;

import java.util.HashMap;
import java.util.Map;

public class GeneralCache {
	private final Map<String, InviteData> inviteMap = new HashMap<>();

	private final GuildSettingsCache guildSettingsCache;
	private final DatabaseManager databaseManager;

	public GeneralCache(GuildSettingsCache guildSettingsCache, DatabaseManager databaseManager) {
		this.guildSettingsCache = guildSettingsCache;
		this.databaseManager = databaseManager;
	}

	public Map<String, InviteData> getInviteCache() {
		return inviteMap;
	}

	public void removeGuild(long guildId) {
		if (guildSettingsCache.isVip(guildId)) {
			return;
		}
		guildSettingsCache.remove(guildId);
		databaseManager.removeGuild(guildId);
	}
}