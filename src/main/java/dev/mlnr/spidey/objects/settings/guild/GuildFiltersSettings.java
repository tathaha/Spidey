package dev.mlnr.spidey.objects.settings.guild;

import dev.mlnr.spidey.DatabaseManager;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class GuildFiltersSettings implements IGuildSettings {
	private final long guildId;

	private boolean pinnedDeletingEnabled;
	private boolean inviteDeletingEnabled;

	// invite filter ignored users
	private final List<Long> ignoredUsers;
	// invite filter ignored roles
	private final List<Long> ignoredRoles;

	private final DatabaseManager databaseManager;

	public GuildFiltersSettings(long guildId, DatabaseManager databaseManager) {
		this.guildId = guildId;

		this.ignoredUsers = new ArrayList<>();
		this.ignoredRoles = new ArrayList<>();

		this.databaseManager = databaseManager;
	}

	public GuildFiltersSettings(long guildId, boolean pinnedDeletingEnabled, boolean inviteDeletingEnabled, List<Long> ignoredUsers, List<Long> ignoredRoles, DatabaseManager databaseManager) {
		this.guildId = guildId;

		this.pinnedDeletingEnabled = pinnedDeletingEnabled;
		this.inviteDeletingEnabled = inviteDeletingEnabled;

		this.ignoredUsers = ignoredUsers;
		this.ignoredRoles = ignoredRoles;

		this.databaseManager = databaseManager;
	}

	// getters

	public boolean isPinnedDeletingEnabled() {
		return pinnedDeletingEnabled;
	}

	public boolean isInviteDeletingEnabled() {
		return inviteDeletingEnabled;
	}

	// setters

	public void setPinnedDeletingEnabled(boolean pinnedDeletingEnabled) {
		this.pinnedDeletingEnabled = pinnedDeletingEnabled;
		databaseManager.setPinnedDeletingEnabled(guildId, pinnedDeletingEnabled);
	}

	public void setInviteDeletingEnabled(boolean inviteDeletingEnabled) {
		this.inviteDeletingEnabled = inviteDeletingEnabled;
		databaseManager.setInviteDeletingEnabled(guildId, inviteDeletingEnabled);
	}

	// invite filter ignored users

	public List<Long> getIgnoredUsers() {
		return ignoredUsers;
	}

	public boolean isUserIgnored(long userId) {
		return ignoredUsers.contains(userId);
	}

	public void addIgnoredUser(long userId) {
		ignoredUsers.add(userId);
		databaseManager.addIgnoredUser(guildId, userId);
	}

	public void removeIgnoredUser(long userId) {
		ignoredUsers.remove(userId);
		databaseManager.removeIgnoredUser(guildId, userId);
	}

	// invite filter ignored roles

	public List<Long> getIgnoredRoles() {
		return ignoredRoles;
	}

	public boolean isRoleIgnored(long roleId) {
		return ignoredRoles.contains(roleId);
	}

	public void addIgnoredRole(long roleId) {
		ignoredRoles.add(roleId);
		databaseManager.addIgnoredRole(guildId, roleId);
	}

	public void removeIgnoredRole(long roleId) {
		ignoredRoles.remove(roleId);
		databaseManager.removeIgnoredRole(guildId, roleId);
	}

	// invite filter helper methods

	public boolean isIgnored(Member member) {
		return isUserIgnored(member.getIdLong()) || member.getRoles().stream().anyMatch(role -> isRoleIgnored(role.getIdLong()));
	}
}