package dev.mlnr.spidey;

import dev.mlnr.spidey.utils.concurrent.ConcurrentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@SuppressWarnings("ConstantConditions")
public class MySQL
{
	private static final Logger LOG = LoggerFactory.getLogger(MySQL.class);
	private static final ExecutorService EXECUTOR_SERVICE = ConcurrentUtils.createThread("Spidey MySQL");

	private MySQL()
	{
		super();
	}

	private static Connection initializeConnection()
	{
		try
		{
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/canelex", "admin", System.getenv("mysql"));
		}
		catch (final Exception ex)
		{
			LOG.error("There was an error establishing the connection to the database!", ex);
		}
		return null;
	}

	// GETTERS

	private static <T> CompletableFuture<String> retrieveQueryFuture(final String query, final long guildId, final String property, final Class<T> type)
	{
		final var isString = type.equals(String.class);
		return CompletableFuture.supplyAsync(() ->
		{
			try (final var db = initializeConnection(); final var ps = db.prepareStatement(query))
			{
				ps.setLong(1, guildId);
				try (final var rs = ps.executeQuery())
				{
					if (!rs.isBeforeFirst())
						return isString ? "" : "0";
					rs.next();
					return isString ? rs.getString(property) : String.valueOf(rs.getLong(property));
				}
			}
			catch (final SQLException ex)
			{
				LOG.error("There was an error while requesting the {} property for guild {}!", property, guildId, ex);
			}
			return isString ? null : "0";
		}, EXECUTOR_SERVICE);
	}

	private static <T> String getQueryResult(final String query, final long guildId, final String property, final Class<T> type)
	{
		try
		{
			return retrieveQueryFuture(query, guildId, property, type).get();
		}
		catch (final Exception ex)
		{
			LOG.error("There was an error while getting the future for the {} property for guild {}!", property, guildId, ex);
		}
		return type.equals(String.class) ? null : "0";
	}

	private static long retrieveLongProperty(final String property, final long guildId)
	{
		return Long.parseLong(getQueryResult("SELECT `" + property + "` FROM `guilds` WHERE `guild_id`=?;", guildId, property, Long.class));
	}

	public static long retrieveChannel(final long guildId)
	{
		return retrieveLongProperty("channel_id", guildId);
	}

	public static long retrieveRole(final long guildId)
	{
		return retrieveLongProperty("role_id", guildId);
	}

	public static String retrievePrefix(final long guildId)
	{
		return getQueryResult("SELECT `prefix` FROM `guilds` WHERE `guild_id`=?;", guildId, "prefix", String.class);
	}

	//SETTERS

	private static <T> void executeSetQuery(final String query, final long guildId, final String property, final T value)
	{
		CompletableFuture.runAsync(() ->
		{
			try (final var db = initializeConnection(); final var ps = db.prepareStatement(query))
			{
				ps.setLong(1, guildId);
				if (value instanceof String)
					ps.setString(2, (String) value);
				else
					ps.setLong(2, (long) value);
				ps.executeUpdate();
			}
			catch (final SQLException ex)
			{
				LOG.error("There was an error while setting the {} property for guild {}!", property, guildId, ex);
			}
		}, EXECUTOR_SERVICE);
	}

	private static <T> void setProperty(final String property, final long guildId, final T value)
	{
		final var query = String.format("INSERT INTO `guilds` (guild_id, %s) VALUES (?, ?) "
											 + "ON DUPLICATE KEY UPDATE %s='%s'", property, property, value);
		executeSetQuery(query, guildId, property, value);
	}

	public static void setChannel(final long guildId, final long value)
	{
		setProperty("channel_id", guildId, value);
	}

	public static void setRole(final long guildId, final long value)
	{
		setProperty("role_id", guildId, value);
	}

	public static void setPrefix(final long guildId, final String value)
	{
		setProperty("prefix", guildId, value);
	}

	// REMOVALS

	public static void removeChannel(final long guildId)
	{
		setChannel(guildId, 0);
	}

	public static void removeRole(final long guildId)
	{
		setRole(guildId, 0);
	}

	public static void removeEntry(final long guildId)
	{
		CompletableFuture.runAsync(() ->
		{
			try (final var db = initializeConnection(); final var ps = db.prepareStatement("DELETE IGNORE FROM `guilds` WHERE `guild_id`=" + guildId))
			{
				ps.executeUpdate();
			}
			catch (final SQLException ex)
			{
				LOG.error("There was an error while removing the entry for guild {}!", guildId, ex);
			}
		}, EXECUTOR_SERVICE);
	}

	// MISC

	public static boolean isVip(final long guildId)
	{
		return retrieveLongProperty("vip", guildId) == 1;
	}

	public static boolean isSupporter(final long guildId)
	{
		return retrieveLongProperty("supporter", guildId) == 1;
	}
}