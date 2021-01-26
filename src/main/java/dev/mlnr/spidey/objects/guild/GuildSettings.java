package dev.mlnr.spidey.objects.guild;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.objects.I18n;

public class GuildSettings {

	private final long guildId;

	private long logChannelId;
	private long joinRoleId;
	private String prefix;
	private final I18n i18n;

	private boolean snipingEnabled;
	private boolean errorCleanupEnabled;
	private boolean vip;

	private long djRoleId;
	private boolean segmentSkippingEnabled;
	private int defaultVolume;

	private boolean fairQueueEnabled;
	private int fairQueueThreshold;

	private final DatabaseManager databaseManager;

	public GuildSettings(long guildId, long logChannelId, long joinRoleId, String prefix, String language, boolean snipingEnabled, boolean errorCleanupEnabled, boolean vip, long djRoleId,
	                     boolean segmentSkippingEnabled, int defaultVolume, boolean fairQueueEnabled, int fairQueueThreshold, DatabaseManager databaseManager) {
		this.guildId = guildId;

		this.logChannelId = logChannelId;
		this.joinRoleId = joinRoleId;
		this.prefix = prefix;
		this.i18n = I18n.ofLanguage(language);

		this.snipingEnabled = snipingEnabled;
		this.errorCleanupEnabled = errorCleanupEnabled;
		this.vip = vip;

		this.djRoleId = djRoleId;
		this.segmentSkippingEnabled = segmentSkippingEnabled;
		this.defaultVolume = defaultVolume;

		this.fairQueueEnabled = fairQueueEnabled;
		this.fairQueueThreshold = fairQueueThreshold;

		this.databaseManager = databaseManager;
	}

	// getters

	public long getLogChannelId() {
		return this.logChannelId;
	}

	public void setLogChannelId(long logChannelId) {
		this.logChannelId = logChannelId;
		databaseManager.setLogChannelId(guildId, logChannelId);
	}

	public long getJoinRoleId() {
		return this.joinRoleId;
	}

	public void setJoinRoleId(long joinRoleId) {
		this.joinRoleId = joinRoleId;
		databaseManager.setJoinRoleId(guildId, joinRoleId);
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
		databaseManager.setPrefix(guildId, prefix);
	}

	public I18n getI18n() {
		return this.i18n;
	}

	// music getters

	public boolean isSnipingEnabled() {
		return this.snipingEnabled;
	}

	public void setSnipingEnabled(boolean enabled) {
		this.snipingEnabled = enabled;
		databaseManager.setSnipingEnabled(guildId, enabled);
	}

	public boolean isErrorCleanupEnabled() {
		return this.errorCleanupEnabled;
	}

	// fair queue getters

	public void setErrorCleanupEnabled(boolean enabled) {
		this.errorCleanupEnabled = enabled;
		databaseManager.setErrorCleanupEnabled(guildId, enabled);
	}

	public boolean isVip() {
		return this.vip;
	}

	// setters

	public void setVip(boolean vip) {
		this.vip = vip;
		databaseManager.setVip(guildId, vip);
	}

	public long getDjRoleId() {
		return this.djRoleId;
	}

	public void setDjRoleId(long djRoleId) {
		this.djRoleId = djRoleId;
		databaseManager.setDJRoleId(guildId, djRoleId);
	}

	public boolean isSegmentSkippingEnabled() {
		return this.segmentSkippingEnabled;
	}

	public void setSegmentSkippingEnabled(boolean enabled) {
		this.segmentSkippingEnabled = enabled;
		databaseManager.setSegmentSkippingEnabled(guildId, enabled);
	}

	public int getDefaultVolume() {
		return this.defaultVolume;
	}

	public void setDefaultVolume(int defaultVolume) {
		this.defaultVolume = defaultVolume;
		databaseManager.setDefaultVolume(guildId, defaultVolume);
	}

	// music setters

	public boolean isFairQueueEnabled() {
		return this.fairQueueEnabled;
	}

	public void setFairQueueEnabled(boolean enabled) {
		this.fairQueueEnabled = enabled;
		databaseManager.setFairQueueEnabled(guildId, enabled);
	}

	public int getFairQueueThreshold() {
		return this.fairQueueThreshold;
	}

	// fair queue setters

	public void setFairQueueThreshold(int threshold) {
		this.fairQueueThreshold = threshold;
		databaseManager.setFairQueueThreshold(guildId, threshold);
	}

//	public void setLanguage(String language) {
//		this.i18n = I18n.ofLanguage(language);
//		databaseManager.setLanguage(guildId, language);
//	}
}