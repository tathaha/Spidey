package dev.mlnr.spidey.objects.data.user;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.utils.FixedSizeList;

import java.util.List;
import java.util.stream.Collectors;

public class UserMusicHistory {
	private final long userId;

	private final FixedSizeList<String> queries;
	private final String type;

	private final DatabaseManager databaseManager;

	public UserMusicHistory(long userId, FixedSizeList<String> queries, String type, DatabaseManager databaseManager) {
		this.userId = userId;

		this.queries = queries;
		this.type = type;

		this.databaseManager = databaseManager;
	}

	public List<String> getLastQueries() {
		return queries.stream().limit(25).collect(Collectors.toList());
	}

	public List<String> getLastQueriesLike(String input) {
		return queries.stream().filter(query -> query.toLowerCase().startsWith(input)).limit(25).collect(Collectors.toList());
	}

	public void saveQuery(String query) {
		if (query.length() > 100) {
			return; // ignore queries that are over 100 characters, Discord limit for choices
		}
		if (queries.contains(query)) {
			queries.remove(query);
			databaseManager.removeFromMusicHistory(userId, query, type);
		}
		queries.add(query);
		databaseManager.saveToMusicHistory(userId, query, type);
	}
}