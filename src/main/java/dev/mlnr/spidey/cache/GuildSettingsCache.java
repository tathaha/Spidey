package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.objects.I18n;
import dev.mlnr.spidey.objects.guild.GuildSettings;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class GuildSettingsCache {
	private final Map<Long, GuildSettings> guildSettingsMap = new HashMap<>();

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

	public static GuildSettingsCache getInstance() {
		return guildSettingsCache;
	}

	// getters

	public long getLogChannelId(long guildId) {
		return getGuildSettings(guildId).getLogChannelId();
	}

	public long getJoinRoleId(long guildId) {
		return getGuildSettings(guildId).getJoinRoleId();
	}

	public String getPrefix(long guildId) {
		return getGuildSettings(guildId).getPrefix();
	}

	public I18n getI18n(long guildId) {
		return getGuildSettings(guildId).getI18n();
	}

	public boolean isSnipingEnabled(long guildId) {
		return getGuildSettings(guildId).isSnipingEnabled();
	}

	public boolean isErrorCleanupEnabled(long guildId) {
		return getGuildSettings(guildId).isErrorCleanupEnabled();
	}

	public boolean isVip(long guildId) {
		return getGuildSettings(guildId).isVip();
	}

	// music getters

	public long getDJRoleId(long guildId) {
		return getGuildSettings(guildId).getDjRoleId();
	}

	public boolean isSegmentSkippingEnabled(long guildId) {
		return getGuildSettings(guildId).isSegmentSkippingEnabled();
	}

	public int getDefaultVolume(long guildId) {
		return getGuildSettings(guildId).getDefaultVolume();
	}

	// fair queue getters

	public  boolean isFairQueueEnabled(long guildId) {
		return getGuildSettings(guildId).isFairQueueEnabled();
	}

	public int getFairQueueThreshold(long guildId) {
		return getGuildSettings(guildId).getFairQueueThreshold();
	}

	// setters

	public void setLogChannelId(long guildId, long logChannelId) {
		getGuildSettings(guildId).setLogChannelId(logChannelId);
	}

	public void setJoinRoleId(long guildId, long joinRoleId) {
		getGuildSettings(guildId).setJoinRoleId(joinRoleId);
	}

	public void setPrefix(long guildId, String prefix) {
		getGuildSettings(guildId).setPrefix(prefix);
	}

//	public void setLanguage(long guildId, String language) {
//		getGuildSettings(guildId).setLanguage(language);
//	}

	public void setSnipingEnabled(long guildId, boolean enabled) {
		getGuildSettings(guildId).setSnipingEnabled(enabled);
	}

	public void setErrorCleanupEnabled(long guildId, boolean enabled) {
		getGuildSettings(guildId).setErrorCleanupEnabled(enabled);
	}

	public void setVip(long guildId, boolean vip) {
		getGuildSettings(guildId).setVip(vip);
	}

	// music setters

	public void setDJRoleId(long guildId, long djRoleId) {
		getGuildSettings(guildId).setDjRoleId(djRoleId);
	}

	public void setSegmentSkippingEnabled(long guildId, boolean enabled) {
		getGuildSettings(guildId).setSegmentSkippingEnabled(enabled);
	}

	public void setDefaultVolume(long guildId, int volume) {
		getGuildSettings(guildId).setDefaultVolume(volume);
	}

	// fair queue setters

	public void setFairQueueEnabled(long guildId, boolean enabled) {
		getGuildSettings(guildId).setFairQueueEnabled(enabled);
	}

	public void setFairQueueThreshold(long guildId, int threshold) {
		getGuildSettings(guildId).setFairQueueThreshold(threshold);
	}

	// removals

	public void removeLogChannel(long guildId) {
		setLogChannelId(guildId, 0);
	}

	public void removeJoinRole(long guildId) {
		setJoinRoleId(guildId, 0);
	}

	public void removeDJRole(long guildId) {
		setDJRoleId(guildId, 0);
	}

	// misc helpers

	public TextChannel getLogChannel(long guildId) {
		var logChannelId = getLogChannelId(guildId);
		return logChannelId == 0 ? null : spidey.getJDA().getTextChannelById(logChannelId);
	}

	public Role getJoinRole(long guildId) {
		var joinRoleId = getJoinRoleId(guildId);
		return joinRoleId == 0 ? null : spidey.getJDA().getRoleById(joinRoleId);
	}

	public Role getDJRole(long guildId) {
		var djRoleId = getDJRoleId(guildId);
		return djRoleId == 0 ? null : spidey.getJDA().getRoleById(djRoleId);
	}

	// other

	public void remove(long guildId) {
		guildSettingsMap.remove(guildId);
	}

	private GuildSettings getGuildSettings(long guildId) {
		return guildSettingsMap.computeIfAbsent(guildId, k -> spidey.getDatabaseManager().retrieveGuildSettings(guildId));
	}
}