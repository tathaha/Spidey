package dev.mlnr.spidey.objects.settings.guild;

import dev.mlnr.spidey.DatabaseManager;

public class GuildFiltersSettings implements IGuildSettings {
	private final long guildId;

	private boolean pinnedDeletingEnabled;

	private final DatabaseManager databaseManager;

	public GuildFiltersSettings(long guildId, boolean pinnedDeletingEnabled, DatabaseManager databaseManager) {
		this.guildId = guildId;

		this.pinnedDeletingEnabled = pinnedDeletingEnabled;

		this.databaseManager = databaseManager;
	}

	// getters

	public boolean isPinnedDeletingEnabled() {
		return this.pinnedDeletingEnabled;
	}

	// setters

	public void setPinnedDeletingEnabled(boolean pinnedDeletingEnabled) {
		this.pinnedDeletingEnabled = pinnedDeletingEnabled;
		databaseManager.setPinnedDeletingEnabled(guildId, pinnedDeletingEnabled);
	}
}