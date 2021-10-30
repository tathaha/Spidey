package dev.mlnr.spidey.objects.data.user;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.utils.FixedSizeList;

import java.util.List;
import java.util.stream.Collectors;

public class UserSearchHistory {
	private final long userId;

	private final FixedSizeList<String> queries;

	private final DatabaseManager databaseManager;

	public UserSearchHistory(long userId, FixedSizeList<String> queries, DatabaseManager databaseManager) {
		this.userId = userId;

		this.queries = queries;

		this.databaseManager = databaseManager;
	}

	public List<String> getLastQueries() {
		return queries.stream().limit(25).collect(Collectors.toList());
	}

	public List<String> getLastQueriesLike(String input) {
		return queries.stream().filter(query -> query.startsWith(input)).collect(Collectors.toList());
	}

	public void saveQuery(String query) {
		queries.add(query);
		databaseManager.saveToSearchHistory(userId, query);
	}
}