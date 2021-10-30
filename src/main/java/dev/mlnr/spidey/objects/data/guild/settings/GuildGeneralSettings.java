package dev.mlnr.spidey.objects.data.guild.settings;

import dev.mlnr.spidey.DatabaseManager;

public class GuildGeneralSettings implements IGuildSettings {
	private final long guildId;

	private boolean vip;

	private final DatabaseManager databaseManager;

	public GuildGeneralSettings(long guildId, DatabaseManager databaseManager) {
		this.guildId = guildId;

		this.databaseManager = databaseManager;
	}

	public GuildGeneralSettings(long guildId, boolean vip, DatabaseManager databaseManager) {
		this.guildId = guildId;

		this.vip = vip;

		this.databaseManager = databaseManager;
	}

	// getters

	public boolean isVip() {
		return this.vip;
	}

	// setters

	public void setVip(boolean vip) {
		this.vip = vip;
		databaseManager.setVip(guildId, vip);
	}
}