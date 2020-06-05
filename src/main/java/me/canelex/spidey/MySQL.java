package me.canelex.spidey;

import me.canelex.spidey.utils.Utils;
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
	private static final Connection DB = initializeConnection();
	private static final ExecutorService EXECUTOR_SERVICE = Utils.createThread("Spidey MySQL");

	private MySQL()
	{
		super();
	}

	private static Connection initializeConnection()
	{
		try
		{
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/canelex", Secrets.USERNAME, Secrets.PASS);
		}
		catch (final Exception ex)
		{
			LOG.error("There was an error establishing the connection to the database!", ex);
		}
		return null;
	}

	// GETTERS

	private static <T> CompletableFuture<String> getQueryFuture(final String query, final long guildId, final String property, final Class<T> type)
	{
		final var isString = type.equals(String.class);
		return CompletableFuture.supplyAsync(() ->
		{
			try (final var ps = DB.prepareStatement(query))
			{
				ps.setLong(1, guildId);
				try (final var rs = ps.executeQuery())
				{
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
			return getQueryFuture(query, guildId, property, type).get();
		}
		catch (final Exception ex)
		{
			LOG.error("There was an error while getting the future for the {} property for guild {}!", property, guildId, ex);
		}
		return type.equals(String.class) ? null : "0";
	}

	private static long getLongProperty(final String property, final long guildId)
	{
		return Long.parseLong(getQueryResult("SELECT `" + property + "` FROM `guilds` WHERE `guild_id`=?;", guildId, property, Long.class));
	}

	public static long getChannel(final long guildId)
	{
		return getLongProperty("channel_id", guildId);
	}

	public static long getRole(final long guildId)
	{
		return getLongProperty("role_id", guildId);
	}

	public static String getPrefix(final long guildId)
	{
		return getQueryResult("SELECT `prefix` FROM `guilds` WHERE `guild_id`=?;", guildId, "prefix", String.class);
	}

	//SETTERS

	private static <T> void executeSetQuery(final String query, final long guildId, final String property, final T value)
	{
		CompletableFuture.runAsync(() ->
		{
			try (final var ps = DB.prepareStatement(query))
			{
				if (value instanceof String)
					ps.setString(1, (String) value);
				else
					ps.setLong(1, (Long) value);
				ps.setLong(2, guildId);
				ps.executeUpdate();
			}
			catch (final SQLException ex)
			{
				LOG.error("There was an error while setting the {} property for guild {}!", property, guildId, ex);
			}
		}, EXECUTOR_SERVICE);
	}

	private static <T> void setLongProperty(final String property, final long guildId, final T value)
	{
		executeSetQuery("UPDATE `guilds` SET `" + property + "`=? WHERE `guild_id`=?;", guildId, property, value);
	}

	public static void setChannel(final long guildId, final long value)
	{
		setLongProperty("channel_id", guildId, value);
	}

	public static void setRole(final long guildId, final long value)
	{
		setLongProperty("role_id", guildId, value);
	}

	public static void setPrefix(final long guildId, final String value)
	{
		executeSetQuery("UPDATE `guilds` SET `prefix`=? WHERE `guild_id`=?;", guildId, "prefix", value);
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
}