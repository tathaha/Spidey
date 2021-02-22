package dev.mlnr.spidey;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.mlnr.spidey.objects.settings.guild.GuildFiltersSettings;
import dev.mlnr.spidey.objects.settings.guild.GuildGeneralSettings;
import dev.mlnr.spidey.objects.settings.guild.GuildMiscSettings;
import dev.mlnr.spidey.objects.settings.guild.GuildMusicSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseManager {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
	private final HikariDataSource hikariDataSource;

	public DatabaseManager() {
		var hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName("org.postgresql.Driver");
		hikariConfig.setJdbcUrl("jdbc:postgresql:spidey");
		hikariConfig.setUsername("sebo");
		hikariConfig.setPassword(System.getenv("db"));
		hikariDataSource = new HikariDataSource(hikariConfig);
	}

	public GuildFiltersSettings retrieveGuildFiltersSettings(long guildId) {
		try (var con = hikariDataSource.getConnection(); var ps = con.prepareStatement("SELECT * FROM settings_filters WHERE guild_id=?")) {
			ps.setLong(1, guildId);
			try (var rs = ps.executeQuery()) {
				return rs.next()
						? new GuildFiltersSettings(guildId, rs.getBoolean("pinned_deleting_enabled"), rs.getBoolean("invite_deleting_enabled"), retrieveInviteFilterIgnoredUsers(guildId),
							retrieveInviteFilterIgnoredRoles(guildId), this)
						: new GuildFiltersSettings(guildId, false, false, Collections.emptyList(), Collections.emptyList(), this); // default settings
			}
		}
		catch (SQLException ex) {
			logger.error("There was an error while requesting the filters settings for guild {}!", guildId, ex);
			return new GuildFiltersSettings(guildId, false, false, Collections.emptyList(), Collections.emptyList(), this); // default settings
		}
	}

	public GuildGeneralSettings retrieveGuildGeneralSettings(long guildId) {
		try (var con = hikariDataSource.getConnection(); var ps = con.prepareStatement("SELECT * FROM guilds WHERE guild_id=?")) {
			ps.setLong(1, guildId);
			try (var rs = ps.executeQuery()) {
				return rs.next()
						? new GuildGeneralSettings(guildId, rs.getBoolean("vip"), this)
						: new GuildGeneralSettings(guildId, false, this); // default settings
			}
		}
		catch (SQLException ex) {
			logger.error("There was an error while requesting the general settings for guild {}!", guildId, ex);
			return new GuildGeneralSettings(guildId, false, this); // default settings
		}
	}

	public GuildMiscSettings retrieveGuildMiscSettings(long guildId, Spidey spidey) {
		try (var con = hikariDataSource.getConnection(); var ps = con.prepareStatement("SELECT * FROM settings_misc WHERE guild_id=?")) {
			ps.setLong(1, guildId);
			try (var rs = ps.executeQuery()) {
				return rs.next()
						? new GuildMiscSettings(guildId, rs.getLong("log_channel_id"), rs.getLong("join_role_id"), rs.getString("prefix"), rs.getString("language"),
							rs.getBoolean("sniping_enabled"), rs.getBoolean("error_cleanup_enabled"), spidey)
						: new GuildMiscSettings(guildId, 0, 0, "s!", "en", true, false, spidey); // default settings
			}
		}
		catch (SQLException ex) {
			logger.error("There was an error while requesting the misc settings for guild {}! Using default settings.", guildId, ex);
			return new GuildMiscSettings(guildId, 0, 0, "s!", "en", true, false, spidey); // default settings
		}
	}

	public GuildMusicSettings retrieveGuildMusicSettings(long guildId, Spidey spidey) {
		try (var con = hikariDataSource.getConnection(); var ps = con.prepareStatement("SELECT * FROM settings_music WHERE guild_id=?")) {
			ps.setLong(1, guildId);
			try (var rs = ps.executeQuery()) {
				return rs.next()
						? new GuildMusicSettings(guildId, rs.getInt("default_volume"), rs.getLong("dj_role_id"), rs.getBoolean("segment_skipping_enabled"),
							rs.getBoolean("fair_queue_enabled"), rs.getInt("fair_queue_threshold"), spidey)
						: new GuildMusicSettings(guildId, 100, 0, false, true, 3, spidey); // default settings
			}
		}
		catch (SQLException ex) {
			logger.error("There was an error while requesting the music settings for guild {}! Using default settings.", guildId, ex);
			return new GuildMusicSettings(guildId, 100, 0, false, true, 3, spidey); // default settings
		}
	}

	public void registerGuild(long guildId) {
		try (var con = hikariDataSource.getConnection(); var ps = con.prepareStatement("INSERT INTO guilds (guild_id) VALUES (?) ON CONFLICT DO NOTHING")) {
			ps.setLong(1, guildId);
			ps.executeUpdate();
		}
		catch (SQLException ex) {
			logger.error("There was an error while adding the entry for guild {}!", guildId, ex);
		}
	}

	public void removeGuild(long guildId) {
		try (var con = hikariDataSource.getConnection(); var ps = con.prepareStatement("DELETE FROM guilds WHERE guild_id=" + guildId)) {
			ps.executeUpdate();
		}
		catch (SQLException ex) {
			logger.error("There was an error while removing the entry for guild {}!", guildId, ex);
		}
	}

	// helper methods

	private <T> void executeSetQuery(String table, String property, long guildId, T value) {
		var query = "INSERT INTO " + table + " (guild_id, " + property + ") VALUES (?, ?) ON CONFLICT (guild_id) DO UPDATE SET " + property + "='" + value + "'";
		try (var con = hikariDataSource.getConnection(); var ps = con.prepareStatement(query)) {
			ps.setLong(1, guildId);
			ps.setObject(2, value);
			ps.executeUpdate();
		}
		catch (SQLException ex) {
			logger.error("There was an error while setting the {} property for guild {}!", property, guildId, ex);
		}
	}

	private <T> void executeFiltersSetQuery(String property, long guildId, T value) {
		executeSetQuery("settings_filters", property, guildId, value);
	}

	private <T> void executeGeneralSetQuery(String property, long guildId, T value) {
		executeSetQuery("guilds", property, guildId, value);
	}

	private <T> void executeMiscSetQuery(String property, long guildId, T value) {
		executeSetQuery("settings_misc", property, guildId, value);
	}

	private <T> void executeMusicSetQuery(String property, long guildId, T value) {
		executeSetQuery("settings_music", property, guildId, value);
	}

	// guild filters setters

	public void setPinnedDeletingEnabled(long guildId, boolean enabled) {
		executeFiltersSetQuery("pinned_deleting_enabled", guildId, enabled);
	}

	public void setInviteDeletingEnabled(long guildId, boolean enabled) {
		executeFiltersSetQuery("invite_deleting_enabled", guildId, enabled);
	}

	// guild general setters

	public void setVip(long guildId, boolean vip) {
		executeGeneralSetQuery("vip", guildId, vip);
	}

	// guild misc setters

	public void setLogChannelId(long guildId, long channelId) {
		executeMiscSetQuery("log_channel_id", guildId, channelId);
	}

	public void setJoinRoleId(long guildId, long roleId) {
		executeMiscSetQuery("join_role_id", guildId, roleId);
	}

	public void setPrefix(long guildId, String prefix) {
		executeMiscSetQuery("prefix", guildId, prefix);
	}

	//	public void setLanguage(long guildId, String language) {
	//		executeMiscSetQuery("language", guildId, language);
	//	}

	public void setSnipingEnabled(long guildId, boolean enabled) {
		executeMiscSetQuery("sniping_enabled", guildId, enabled);
	}

	public void setErrorCleanupEnabled(long guildId, boolean enabled) {
		executeMiscSetQuery("error_cleanup_enabled", guildId, enabled);
	}

	// guild music setters

	public void setDefaultVolume(long guildId, int defaultVolume) {
		executeMusicSetQuery("default_volume", guildId, defaultVolume);
	}

	public void setDJRoleId(long guildId, long djRoleId) {
		executeMusicSetQuery("dj_role_id", guildId, djRoleId);
	}

	public void setFairQueueEnabled(long guildId, boolean enabled) {
		executeMusicSetQuery("fair_queue_enabled", guildId, enabled);
	}

	public void setFairQueueThreshold(long guildId, int threshold) {
		executeMusicSetQuery("fair_queue_threshold", guildId, threshold);
	}

	public void setSegmentSkippingEnabled(long guildId, boolean enabled) {
		executeMusicSetQuery("segment_skipping_enabled", guildId, enabled);
	}

	// invite filter ignored lists

	public List<Long> retrieveInviteFilterIgnoredList(String table, String property, long guildId) {
		var query = "SELECT * FROM " + table + " WHERE guild_id=?";
		try (var con = hikariDataSource.getConnection(); var ps = con.prepareStatement(query)) {
			ps.setLong(1, guildId);
			try (var rs = ps.executeQuery()) {
				var ignored = new ArrayList<Long>();
				while (rs.next()) {
					ignored.add(rs.getLong(property));
				}
				return ignored;
			}
		}
		catch (SQLException ex) {
			logger.error("There was an error while retrieving the ignored {} list for invite filter for guild {}!", property, guildId, ex);
			return Collections.emptyList();
		}
	}

	public List<Long> retrieveInviteFilterIgnoredUsers(long guildId) {
		return retrieveInviteFilterIgnoredList("invite_filter_ignored_users", "user_id", guildId);
	}

	public List<Long> retrieveInviteFilterIgnoredRoles(long guildId) {
		return retrieveInviteFilterIgnoredList("invite_filter_ignored_roles", "role_id", guildId);
	}

	public void executeInviteFilterAddQuery(String table, String property, long guildId, long id) {
		var query = "INSERT INTO " + table + " (guild_id, " + property + ") VALUES (?, ?)";
		try (var con = hikariDataSource.getConnection(); var ps = con.prepareStatement(query)) {
			ps.setLong(1, guildId);
			ps.setLong(2, id);
			ps.executeUpdate();
		}
		catch (SQLException ex) {
			logger.error("There was an error while adding an ignored {} for invite filter for guild {}!", property, guildId, ex);
		}
	}

	public void executeInviteFilterRemoveQuery(String table, String property, long guildId, long id) {
		var query = "DELETE FROM " + table + " WHERE guild_id=? AND " + property + "=?";
		try (var con = hikariDataSource.getConnection(); var ps = con.prepareStatement(query)) {
			ps.setLong(1, guildId);
			ps.setLong(2, id);
			ps.executeUpdate();
		}
		catch (SQLException ex) {
			logger.error("There was an error while removing an ignored {} for invite filter for guild {}!", property, guildId, ex);
		}
	}

	public void addIgnoredUser(long guildId, long userId) {
		executeInviteFilterAddQuery("invite_filter_ignored_users", "user_id", guildId, userId);
	}

	public void addIgnoredRole(long guildId, long roleId) {
		executeInviteFilterAddQuery("invite_filter_ignored_roles", "role_id", guildId, roleId);
	}

	public void removeIgnoredUser(long guildId, long userId) {
		executeInviteFilterRemoveQuery("invite_filter_ignored_users", "user_id", guildId, userId);
	}

	public void removeIgnoredRole(long guildId, long roleId) {
		executeInviteFilterRemoveQuery("invite_filter_ignored_roles", "role_id", guildId, roleId);
	}
}
