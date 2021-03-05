package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.objects.settings.guild.*;

import java.util.HashMap;
import java.util.Map;

public class GuildSettingsCache {
	private final Map<Class<?>, Map<Long, IGuildSettings>> guildSettingsMap = new HashMap<>();

	private final Spidey spidey;

	private static GuildSettingsCache guildSettingsCache;

	public GuildSettingsCache(Spidey spidey) {
		this.spidey = spidey;
	}

	public static synchronized GuildSettingsCache getInstance(Spidey spidey) {
		if (guildSettingsCache == null)
			guildSettingsCache = new GuildSettingsCache(spidey);
		return guildSettingsCache;
	}

	public static synchronized GuildSettingsCache getInstance() {
		return guildSettingsCache;
	}

	public GuildChannelsSettings getChannelsSettings(long guildId) {
		return getSettings(GuildChannelsSettings.class, guildId);
	}

	public GuildFiltersSettings getFiltersSettings(long guildId) {
		return getSettings(GuildFiltersSettings.class, guildId);
	}

	public GuildGeneralSettings getGeneralSettings(long guildId) {
		return getSettings(GuildGeneralSettings.class, guildId);
	}

	public GuildMiscSettings getMiscSettings(long guildId) {
		return getSettings(GuildMiscSettings.class, guildId);
	}

	public GuildMusicSettings getMusicSettings(long guildId) {
		return getSettings(GuildMusicSettings.class, guildId);
	}

	public void remove(long guildId) {
		guildSettingsMap.values().forEach(cacheMap -> cacheMap.remove(guildId));
	}

	// helper methods

	private <T extends IGuildSettings> T getSettings(Class<T> type, long guildId) {
		var cacheMap = guildSettingsMap.computeIfAbsent(type, k -> new HashMap<>());
		var settings = cacheMap.get(guildId);
		if (settings == null) {
			settings = parseSettingsFromType(type, guildId);
			cacheMap.put(guildId, settings);
		}
		return (T) settings;
	}

	private IGuildSettings parseSettingsFromType(Class<?> type, long guildId) {
		var databaseManager = spidey.getDatabaseManager();
		if (type == GuildChannelsSettings.class)
			return databaseManager.retrieveGuildChannelsSettings(guildId, databaseManager);
		else if (type == GuildFiltersSettings.class)
			return databaseManager.retrieveGuildFiltersSettings(guildId);
		else if (type == GuildGeneralSettings.class)
			return databaseManager.retrieveGuildGeneralSettings(guildId);
		else if (type == GuildMiscSettings.class)
			return databaseManager.retrieveGuildMiscSettings(guildId, spidey);
		return databaseManager.retrieveGuildMusicSettings(guildId, spidey);
	}
}