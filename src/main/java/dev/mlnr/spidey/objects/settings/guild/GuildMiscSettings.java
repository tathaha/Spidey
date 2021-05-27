package dev.mlnr.spidey.objects.settings.guild;

import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.objects.I18n;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class GuildMiscSettings implements IGuildSettings {
	private final long guildId;

	private long logChannelId;
	private long joinRoleId;
	private final I18n i18n;

	private boolean snipingEnabled = true;
	private boolean errorCleanupEnabled;

	private final Spidey spidey;

	public GuildMiscSettings(long guildId, Spidey spidey) {
		this.guildId = guildId;

		this.i18n = I18n.ofLanguage("en");

		this.spidey = spidey;
	}

	public GuildMiscSettings(long guildId, long logChannelId, long joinRoleId, String language, boolean snipingEnabled, boolean errorCleanupEnabled, Spidey spidey) {
		this.guildId = guildId;

		this.logChannelId = logChannelId;
		this.joinRoleId = joinRoleId;
		this.i18n = I18n.ofLanguage(language);

		this.snipingEnabled = snipingEnabled;
		this.errorCleanupEnabled = errorCleanupEnabled;

		this.spidey = spidey;
	}

	// getters

	public long getLogChannelId() {
		return this.logChannelId;
	}

	public long getJoinRoleId() {
		return this.joinRoleId;
	}

	public I18n getI18n() {
		return this.i18n;
	}

	public boolean isSnipingEnabled() {
		return this.snipingEnabled;
	}

	public boolean isErrorCleanupEnabled() {
		return this.errorCleanupEnabled;
	}

	// setters

	public void setLogChannelId(long logChannelId) {
		this.logChannelId = logChannelId;
		spidey.getDatabaseManager().setLogChannelId(guildId, logChannelId);
	}

	public void setJoinRoleId(long joinRoleId) {
		this.joinRoleId = joinRoleId;
		spidey.getDatabaseManager().setJoinRoleId(guildId, joinRoleId);
	}

	//	public void setLanguage(String language) {
	//		this.i18n = I18n.ofLanguage(language);
	//		databaseManager.setLanguage(guildId, language);
	//	}

	public void setSnipingEnabled(boolean enabled) {
		this.snipingEnabled = enabled;
		spidey.getDatabaseManager().setSnipingEnabled(guildId, enabled);
	}

	public void setErrorCleanupEnabled(boolean enabled) {
		this.errorCleanupEnabled = enabled;
		spidey.getDatabaseManager().setErrorCleanupEnabled(guildId, enabled);
	}

	// helper methods

	public TextChannel getLogChannel() {
		return logChannelId == 0 ? null : spidey.getJDA().getTextChannelById(logChannelId);
	}

	public Role getJoinRole() {
		return joinRoleId == 0 ? null : spidey.getJDA().getRoleById(joinRoleId);
	}

	public void removeLogChannel() {
		setLogChannelId(0);
	}

	public void removeJoinRole() {
		setJoinRoleId(0);
	}
}