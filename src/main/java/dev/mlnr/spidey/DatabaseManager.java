package dev.mlnr.spidey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("ConstantConditions")
public class DatabaseManager
{
	private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);

	private DatabaseManager()
	{
		super();
	}

	private static Connection initializeConnection()
	{
		try
		{
			return DriverManager.getConnection("jdbc:postgresql:spidey", "cane", System.getenv("db"));
		}
		catch (final Exception ex)
		{
			LOG.error("There was an error establishing the connection to the database!", ex);
		}
		return null;
	}

	private static <T> String executeGetQuery(final String property, final long guildId, final Class<T> resultType)
	{
		try (final var db = initializeConnection(); final var ps = db.prepareStatement("SELECT " + property + " FROM guilds WHERE guild_id=?"))
		{
			ps.setLong(1, guildId);
			try (final var rs = ps.executeQuery())
			{
				if (!rs.next())
					return resultType.equals(String.class) ? "" : "0";
				return rs.getString(property);
			}
		}
		catch (final SQLException ex)
		{
			LOG.error("There was an error while requesting the {} property for guild {}!", property, guildId, ex);
		}
		return null;
	}

	private static <T> void executeSetQuery(final String property, final long guildId, final T value)
	{
		final var query = "INSERT INTO guilds (guild_id, " + property + ") VALUES (?, ?) ON CONFLICT (guild_id) DO UPDATE SET " + property + "='" + value + "'";
		try (final var db = initializeConnection(); final var ps = db.prepareStatement(query))
		{
			ps.setLong(1, guildId);
			ps.setObject(2, value);
			ps.executeUpdate();
		}
		catch (final SQLException ex)
		{
			LOG.error("There was an error while setting the {} property for guild {}!", property, guildId, ex);
		}
	}

	// HELPER GETTERS

	private static String getPropertyAsString(final String property, final long guildId)
	{
		return executeGetQuery(property, guildId, String.class);
	}

	private static long getPropertyAsLong(final String property, final long guildId)
	{
		return Long.parseLong(executeGetQuery(property, guildId, long.class));
	}

	// GETTERS

	public static long retrieveChannel(final long guildId)
	{
		return getPropertyAsLong("channel_id", guildId);
	}

	public static long retrieveRole(final long guildId)
	{
		return getPropertyAsLong("role_id", guildId);
	}

	public static String retrievePrefix(final long guildId)
	{
		return getPropertyAsString("prefix", guildId);
	}

	// SETTERS

	public static void setChannel(final long guildId, final long value)
	{
		executeSetQuery("channel_id", guildId, value);
	}

	public static void setRole(final long guildId, final long value)
	{
		executeSetQuery("role_id", guildId, value);
	}

	public static void setPrefix(final long guildId, final String value)
	{
		executeSetQuery("prefix", guildId, value);
	}

	// REMOVALS

	public static void removeEntry(final long guildId)
	{
		try (final var db = initializeConnection(); final var ps = db.prepareStatement("DELETE FROM guilds WHERE guild_id=" + guildId))
		{
			ps.executeUpdate();
		}
		catch (final SQLException ex)
		{
			LOG.error("There was an error while removing the entry for guild {}!", guildId, ex);
		}
	}

	// MISC

	public static boolean isVip(final long guildId)
	{
		return getPropertyAsString("vip", guildId).equals("t");
	}
}