package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.objects.settings.guild.*;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class GuildSettingsCache {
	private final Map<SettingsType, Map<Long, IGuildSettings>> guildSettingsMap = new EnumMap<>(SettingsType.class);

	private final Spidey spidey;

	private static GuildSettingsCache guildSettingsCache;

	private GuildSettingsCache(Spidey spidey) {
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

	public GuildGeneralSettings getGeneralSettings(long guildId) {
		return getSettings(SettingsType.GENERAL, guildId);
	}

	public GuildMiscSettings getMiscSettings(long guildId) {
		return getSettings(SettingsType.MISC, guildId);
	}

	public GuildMusicSettings getMusicSettings(long guildId) {
		return getSettings(SettingsType.MUSIC, guildId);
	}

	public void remove(long guildId) {
		guildSettingsMap.values().forEach(cacheMap -> cacheMap.remove(guildId));
	}

	// helper methods

	private <T extends IGuildSettings> T getSettings(SettingsType type, long guildId) {
		var cacheMap = guildSettingsMap.computeIfAbsent(type, k -> new HashMap<>());
		var settings = cacheMap.get(guildId);
		if (settings == null) {
			settings = parseSettingsFromType(type, guildId);
			cacheMap.put(guildId, settings);
		}
		return (T) settings;
	}

	private IGuildSettings parseSettingsFromType(SettingsType type, long guildId) {
		var databaseManager = spidey.getDatabaseManager();
		switch (type) {
			case GENERAL:
				return databaseManager.retrieveGuildGeneralSettings(guildId);
			case MISC:
				return databaseManager.retrieveGuildMiscSettings(guildId, spidey);
			case MUSIC:
				return databaseManager.retrieveGuildMusicSettings(guildId, spidey);
			default:
				return null;
		}
	}

	private enum SettingsType {
		GENERAL,
		MISC,
		MUSIC
	}
}