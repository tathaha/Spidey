package me.canelex.spidey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("ConstantConditions")
public class MySQL
{
	private MySQL()
	{
		super();
	}

	private static final Logger LOG = LoggerFactory.getLogger(MySQL.class);

	private static Connection getConnection()
	{
		try
		{
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/canelex", Secrets.USERNAME, Secrets.PASS);
		}
		catch (final SQLException e)
		{
			LOG.error("There was an error establishing the connection to the database!", e);
		}
		return null;
	}

	public static long getChannel(final long guildId)
	{
		try (final var c = getConnection(); final var ps = c.prepareStatement("SELECT `channel_id` FROM `guilds` WHERE `guild_id`=?;"))
		{
			ps.setLong(1, guildId);
			try (final var rs = ps.executeQuery())
			{
				rs.next();
				return rs.getLong("channel_id");
			}
		}
		catch (final SQLException e)
		{
			LOG.error("There was an error while requesting the channel_id property for guild {}!", guildId);
		}
		return 0;
	}

	public static long getRole(final long guildId)
	{
		try (final var c = getConnection(); final var ps = c.prepareStatement("SELECT `role_id` FROM `guilds` WHERE `guild_id`=?;"))
		{
			ps.setLong(1, guildId);
			try (final var rs = ps.executeQuery())
			{
				rs.next();
				return rs.getLong("role_id");
			}
		}
		catch (final SQLException e)
		{
			LOG.error("There was an error while requesting the role_id property for guild {}!", guildId);
		}
		return 0;
	}

	public static String getPrefix(final long guildId)
	{
		try (final var c = getConnection(); final var ps = c.prepareStatement("SELECT `prefix` FROM `guilds` WHERE `guild_id`=?;"))
		{
			ps.setLong(1, guildId);
			try (final var rs = ps.executeQuery())
			{
				rs.next();
				return rs.getString("prefix");
			}
		}
		catch (final SQLException e)
		{
			LOG.error("There was an error while requesting the prefix property for guild {}!", guildId);
		}
		return "";
	}

	private static void setProperty(final long guildId, final String value, final String property)
	{
		try (final var c = getConnection(); final var ps = c.prepareStatement("REPLACE INTO `guilds` (`guild_id`, `channel_id`, `role_id`, `prefix`) VALUES (?, ?, ?, ?);"))
		{
			ps.setLong(1, guildId);
			ps.setLong(2, (property.equals("channel_id") ? Long.parseLong(value) : getChannel(guildId)));
			ps.setLong(3, (property.equals("role_id") ? Long.parseLong(value) : getRole(guildId)));
			ps.setString(4, (property.equals("prefix") ? value : getPrefix(guildId)));
			ps.executeUpdate();
		}
		catch (final SQLException e)
		{
			LOG.error("There was an error while upserting the {} property for guild {}!", property, guildId);
		}
	}

	private static void removeProperty(final long guildId, final String property)
	{
		try (final var c = getConnection(); final var ps = c.prepareStatement("UPDATE `guilds` SET `" + property + "`=0 WHERE `guild_id`=?;"))
		{
			ps.setLong(1, guildId);
			ps.executeUpdate();
		}
		catch (final SQLException e)
		{
			LOG.error("There was an error while removing the {} property of guild {}!", property, guildId);
		}
	}

	public static void setChannel(final long guildId, final long id)
	{
		setProperty(guildId, Long.toString(id), "channel_id");
	}

	public static void setRole(final long guildId, final long id)
	{
		setProperty(guildId, Long.toString(id), "role_id");
	}

	public static void setPrefix(final long guildId, final String prefix)
	{
		setProperty(guildId, prefix, "prefix");
	}

	public static void removeChannel(final long guildId)
	{
		removeProperty(guildId, "channel_id");
	}

	public static void removeRole(final long guildId)
	{
		removeProperty(guildId, "role_id");
	}
}