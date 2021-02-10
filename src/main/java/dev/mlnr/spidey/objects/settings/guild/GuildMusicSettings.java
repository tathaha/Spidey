package dev.mlnr.spidey.objects.settings.guild;

import dev.mlnr.spidey.Spidey;
import net.dv8tion.jda.api.entities.Role;

public class GuildMusicSettings implements IGuildSettings {
	private final long guildId;

	private int defaultVolume;
	private long djRoleId;
	private boolean segmentSkippingEnabled;

	private boolean fairQueueEnabled;
	private int fairQueueThreshold;

	private final Spidey spidey;

	public GuildMusicSettings(long guildId, int defaultVolume, long djRoleId, boolean segmentSkippingEnabled, boolean fairQueueEnabled, int fairQueueThreshold, Spidey spidey) {
		this.guildId = guildId;

		this.defaultVolume = defaultVolume;
		this.djRoleId = djRoleId;
		this.segmentSkippingEnabled = segmentSkippingEnabled;

		this.fairQueueEnabled = fairQueueEnabled;
		this.fairQueueThreshold = fairQueueThreshold;

		this.spidey = spidey;
	}

	// getters

	public int getDefaultVolume() {
		return defaultVolume;
	}

	public long getDJRoleId() {
		return djRoleId;
	}

	public boolean isSegmentSkippingEnabled() {
		return segmentSkippingEnabled;
	}

	public boolean isFairQueueEnabled() {
		return fairQueueEnabled;
	}

	public int getFairQueueThreshold() {
		return fairQueueThreshold;
	}

	// setters

	public void setDefaultVolume(int defaultVolume) {
		this.defaultVolume = defaultVolume;
		spidey.getDatabaseManager().setDefaultVolume(guildId, defaultVolume);
	}

	public void setDJRoleId(long djRoleId) {
		this.djRoleId = djRoleId;
		spidey.getDatabaseManager().setDJRoleId(guildId, djRoleId);
	}

	public void setSegmentSkippingEnabled(boolean segmentSkippingEnabled) {
		this.segmentSkippingEnabled = segmentSkippingEnabled;
		spidey.getDatabaseManager().setSegmentSkippingEnabled(guildId, segmentSkippingEnabled);
	}

	public void setFairQueueEnabled(boolean fairQueueEnabled) {
		this.fairQueueEnabled = fairQueueEnabled;
		spidey.getDatabaseManager().setFairQueueEnabled(guildId, fairQueueEnabled);
	}

	public void setFairQueueThreshold(int fairQueueThreshold) {
		this.fairQueueThreshold = fairQueueThreshold;
		spidey.getDatabaseManager().setFairQueueThreshold(guildId, fairQueueThreshold);
	}

	// helper methods

	public Role getDJRole() {
		return djRoleId == 0 ? null : spidey.getJDA().getRoleById(djRoleId);
	}

	public void removeDJRole() {
		setDJRoleId(0);
	}
}