package dev.mlnr.spidey.cache.music;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.objects.data.user.UserSearchHistory;
import net.jodah.expiringmap.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class SearchHistoryCache {
	private final Map<Long, UserSearchHistory> historyMap = ExpiringMap.builder()
			.expirationPolicy(ExpirationPolicy.CREATED)
			.expiration(10, TimeUnit.MINUTES)
			.build();

	private final DatabaseManager databaseManager;

	public SearchHistoryCache(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	// getting history

	private UserSearchHistory getSearchHistory(long userId) {
		return historyMap.computeIfAbsent(userId, k -> databaseManager.retrieveSearchHistory(userId));
	}

	public List<String> getLastQueries(long userId) {
		return getSearchHistory(userId).getLastQueries();
	}

	public List<String> getLastQueriesLike(long userId, String input) {
		return getSearchHistory(userId).getLastQueriesLike(input);
	}

	// saving to history

	public void saveQuery(long userId, String query) {
		getSearchHistory(userId).saveQuery(query);
	}
}