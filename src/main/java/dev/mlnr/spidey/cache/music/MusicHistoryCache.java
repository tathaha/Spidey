package dev.mlnr.spidey.cache.music;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.objects.data.user.UserMusicHistory;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MusicHistoryCache {
	private final Map<Long, Map<String, UserMusicHistory>> historyMap = ExpiringMap.builder()
			.expirationPolicy(ExpirationPolicy.CREATED)
			.expiration(10, TimeUnit.MINUTES)
			.build();

	private final DatabaseManager databaseManager;

	public MusicHistoryCache(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	// getting history

	private UserMusicHistory getMusicHistory(long userId, String type) {
		var cacheMap = historyMap.computeIfAbsent(userId, k -> new HashMap<>());
		return cacheMap.computeIfAbsent(type, k -> databaseManager.retrieveMusicHistory(userId, type));
	}

	public List<String> getLastQueries(long userId, String type) {
		return getMusicHistory(userId, type).getLastQueries();
	}

	public List<String> getLastQueriesLike(long userId, String input, String type) {
		return getMusicHistory(userId, type).getLastQueriesLike(input);
	}

	// saving to history

	public void saveQuery(long userId, String query, String type) {
		getMusicHistory(userId, type).saveQuery(query);
	}
}