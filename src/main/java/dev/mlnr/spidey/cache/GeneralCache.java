package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.DatabaseManager;

public class GeneralCache {
	private final GuildSettingsCache guildSettingsCache;
	private final DatabaseManager databaseManager;

	public GeneralCache(GuildSettingsCache guildSettingsCache, DatabaseManager databaseManager) {
		this.guildSettingsCache = guildSettingsCache;
		this.databaseManager = databaseManager;
	}

	public void removeGuild(long guildId) {
		if (guildSettingsCache.getGeneralSettings(guildId).isVip()) {
			return;
		}
		guildSettingsCache.remove(guildId);
		databaseManager.removeGuild(guildId);
	}
}