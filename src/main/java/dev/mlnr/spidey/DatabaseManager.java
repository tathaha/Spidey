package dev.mlnr.spidey;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.mlnr.spidey.jooq.tables.records.*;
import dev.mlnr.spidey.objects.data.guild.settings.*;
import dev.mlnr.spidey.objects.data.user.UserMusicHistory;
import dev.mlnr.spidey.utils.FixedSizeList;
import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.function.Function;

import static dev.mlnr.spidey.jooq.Tables.*;

public class DatabaseManager {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
	private static final String MUSIC_HISTORY_INSERT = "begin transaction;\n" +
			"insert into music_history (user_id, query, type) values (?, ?, ?);\n" +
			"delete from music_history where user_id = ? and entry_time <\n" +
			"      (select min(entry_time)\n" +
			"       from (select entry_time from music_history where user_id = ? order by entry_time desc limit 100)\n" +
			"                as times);\n" +
			"commit transaction;";
	private final DSLContext ctx;
	private final DataSource dataSource;

	public DatabaseManager() {
		var hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName("org.postgresql.Driver");
		hikariConfig.setJdbcUrl("jdbc:postgresql:spidey");
		hikariConfig.setUsername("sebo");
		hikariConfig.setPassword(System.getenv("db"));
		hikariConfig.setMaximumPoolSize(5);

		var defaultConfig = new DefaultConfiguration();
		var dataSource = new HikariDataSource(hikariConfig);
		defaultConfig.setDataSource(dataSource);
		defaultConfig.setSQLDialect(SQLDialect.POSTGRES);

		this.ctx = DSL.using(defaultConfig);
		this.dataSource = dataSource;
	}

	// getting settings

	public IGuildSettings retrieveGuildSettings(Table<? extends Record> table, long guildId, Function<Record, IGuildSettings> defaultSettingsTransformer,
	                                            Function<Record, IGuildSettings> transformer) {
		try (var selectStep = ctx.selectFrom(table); var whereStep = selectStep.where(guildIdEquals(table, guildId))) {
			var result = whereStep.fetch();
			if (result.isEmpty()) {
				return defaultSettingsTransformer.apply(null);
			}
			return transformer.apply(result.get(0));
		}
		catch (DataAccessException ex) {
			logger.error("There was an error while fetching the {} for guild {}", table.getName(), guildId, ex);
		}
		return defaultSettingsTransformer.apply(null);
	}

	public GuildGeneralSettings retrieveGuildGeneralSettings(long guildId) {
		return (GuildGeneralSettings) retrieveGuildSettings(GUILDS, guildId,
				defaultRecord -> new GuildGeneralSettings(guildId, this),
				settingsRecord -> {
					var casted = (GuildsRecord) settingsRecord;
					return new GuildGeneralSettings(guildId, casted.getVip(), this);
				});
	}

	public GuildMiscSettings retrieveGuildMiscSettings(long guildId, Spidey spidey) {
		return (GuildMiscSettings) retrieveGuildSettings(SETTINGS_MISC, guildId,
				defaultRecord -> new GuildMiscSettings(guildId, spidey),
				settingsRecord -> {
					var casted = (SettingsMiscRecord) settingsRecord;
					return new GuildMiscSettings(guildId, casted.getLogChannelId(), casted.getJoinRoleId(), casted.getLanguage(),
							casted.getSnipingEnabled(), spidey);
				});
	}

	public GuildMusicSettings retrieveGuildMusicSettings(long guildId, Spidey spidey) {
		return (GuildMusicSettings) retrieveGuildSettings(SETTINGS_MUSIC, guildId,
				defaultRecord -> new GuildMusicSettings(guildId, spidey),
				settingsRecord -> {
					var casted = (SettingsMusicRecord) settingsRecord;
					return new GuildMusicSettings(guildId, casted.getDefaultVolume(), casted.getDjRoleId(), casted.getSegmentSkippingEnabled(),
							casted.getFairQueueEnabled(), casted.getFairQueueThreshold(), spidey);
				});
	}

	// registering/removing guild

	public void registerGuild(long guildId) {
		try (var valueStep = ctx.insertInto(GUILDS).columns(GUILDS.GUILD_ID).values(guildId)) {
			valueStep.onConflictDoNothing().execute();
		}
		catch (DataAccessException ex) {
			logger.error("There was an error while registering guild {}", guildId, ex);
		}
	}

	public void removeGuild(long guildId) {
		try (var deleteStep = ctx.deleteFrom(GUILDS); var whereStep = deleteStep.where(GUILDS.GUILD_ID.eq(guildId))) {
			whereStep.execute();
		}
		catch (DataAccessException ex) {
			logger.error("There was an error while removing the entry for guild {}", guildId, ex);
		}
	}

	// helper set methods

	public <T> void executeSetQuery(Table<? extends Record> table, Field<T> column, T value, long guildId) {
		try (var insertStep = ctx.insertInto(table).columns(GUILDS.GUILD_ID, column).values(guildId, value);
		     var setStep = insertStep.onDuplicateKeyUpdate().set(column, value)) {
			setStep.execute();
		}
		catch (DataAccessException ex) {
			logger.error("There was an error while setting the {} column in table {} for guild {}", column.getName(), table.getName(), guildId, ex);
		}
	}

	public <T> void executeGeneralSetQuery(Field<T> column, T value, long guildId) {
		executeSetQuery(GUILDS, column, value, guildId);
	}

	public <T> void executeMiscSetQuery(Field<T> column, T value, long guildId) {
		executeSetQuery(SETTINGS_MISC, column, value, guildId);
	}

	public <T> void executeMusicSetQuery(Field<T> column, T value, long guildId) {
		executeSetQuery(SETTINGS_MUSIC, column, value, guildId);
	}

	// guild general setters

	public void setVip(long guildId, boolean vip) {
		executeGeneralSetQuery(GUILDS.VIP, vip, guildId);
	}

	// guild misc setters

	public void setLogChannelId(long guildId, long channelId) {
		executeMiscSetQuery(SETTINGS_MISC.LOG_CHANNEL_ID, channelId, guildId);
	}

	public void setJoinRoleId(long guildId, long roleId) {
		executeMiscSetQuery(SETTINGS_MISC.JOIN_ROLE_ID, roleId, guildId);
	}

	//	public void setLanguage(long guildId, String language) {
	//		executeMiscSetQuery(SETTINGS_MISC.LANGUAGE, language, guildId);
	//	}

	public void setSnipingEnabled(long guildId, boolean enabled) {
		executeMiscSetQuery(SETTINGS_MISC.SNIPING_ENABLED, enabled, guildId);
	}

	// guild music setters

	public void setDefaultVolume(long guildId, int defaultVolume) {
		executeMusicSetQuery(SETTINGS_MUSIC.DEFAULT_VOLUME, defaultVolume, guildId);
	}

	public void setDJRoleId(long guildId, long djRoleId) {
		executeMusicSetQuery(SETTINGS_MUSIC.DJ_ROLE_ID, djRoleId, guildId);
	}

	public void setFairQueueEnabled(long guildId, boolean enabled) {
		executeMusicSetQuery(SETTINGS_MUSIC.FAIR_QUEUE_ENABLED, enabled, guildId);
	}

	public void setFairQueueThreshold(long guildId, int threshold) {
		executeMusicSetQuery(SETTINGS_MUSIC.FAIR_QUEUE_THRESHOLD, threshold, guildId);
	}

	public void setSegmentSkippingEnabled(long guildId, boolean enabled) {
		executeMusicSetQuery(SETTINGS_MUSIC.SEGMENT_SKIPPING_ENABLED, enabled, guildId);
	}

	// getting user music history

	public UserMusicHistory retrieveMusicHistory(long userId, String type) {
		var queries = new FixedSizeList<String>(100);
		try (var selectStep = ctx.selectFrom(MUSIC_HISTORY); var whereStep = selectStep.where(userIdEquals(MUSIC_HISTORY, userId))
				.and(MUSIC_HISTORY.TYPE.eq(type)).orderBy(MUSIC_HISTORY.ENTRY_TIME.desc())) {
			var results = whereStep.fetch().getValues(MUSIC_HISTORY.QUERY, String.class);
			queries.addAll(results);
		}
		catch (DataAccessException ex) {
			logger.error("There was an error while retrieving the music history of type {} for user {}", type, userId, ex);
		}
		return new UserMusicHistory(userId, queries, type, this);
	}

	// adding to user music history

	public void saveToMusicHistory(long userId, String query, String type) {
		try (var connection = dataSource.getConnection(); var stmt = connection.prepareStatement(MUSIC_HISTORY_INSERT)) { // fuck jooq
			stmt.setLong(1, userId);
			stmt.setString(2, query);
			stmt.setString(3, type);
			stmt.setLong(4, userId);
			stmt.setLong(5, userId);
			stmt.executeUpdate();
		}
		catch (SQLException ex) {
			logger.error("There was an error while adding query {} to music history of type {} of user {}", query, type, userId, ex);
		}
	}

	// removing from user music history

	public void removeFromMusicHistory(long userId, String query, String type) {
		try (var deleteStep = ctx.deleteFrom(MUSIC_HISTORY); var whereStep = deleteStep.where(userIdEquals(MUSIC_HISTORY, userId))
				.and(MUSIC_HISTORY.QUERY.eq(query)).and(MUSIC_HISTORY.TYPE.eq(type))) {
			whereStep.execute();
		}
		catch (DataAccessException ex) {
			logger.error("There was an error while removing query {} from music history of type {} of user {}", query, type, userId, ex);
		}
	}

	// jooq

	private Condition guildIdEquals(Table<? extends Record> table, long guildId) {
		return table.field("guild_id", Long.class).eq(guildId);
	}

	private Condition userIdEquals(Table<? extends Record> table, long userId) {
		return table.field("user_id", Long.class).eq(userId);
	}
}