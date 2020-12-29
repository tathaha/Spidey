package dev.mlnr.spidey;

import dev.mlnr.spidey.objects.guild.GuildSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("ConstantConditions")
public class DatabaseManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);

    private DatabaseManager() {}

    private static Connection initializeConnection()
    {
        try
        {
            return DriverManager.getConnection("jdbc:postgresql:spidey", "sebo", System.getenv("db"));
        }
        catch (final Exception ex)
        {
            LOGGER.error("There was an error establishing the connection to the database!", ex);
        }
        return null;
    }

    public static GuildSettings retrieveGuildSettings(final long guildId)
    {
        try (final var db = initializeConnection(); final var ps = db.prepareStatement("SELECT * FROM guilds WHERE guild_id=?"))
        {
            ps.setLong(1, guildId);
            try (final var rs = ps.executeQuery())
            {
                return rs.next() ? new GuildSettings(guildId, rs.getLong("log_channel_id"), rs.getLong("join_role_id"), rs.getString("prefix"),
                        rs.getBoolean("sniping"), rs.getBoolean("vip"), rs.getLong("music_dj_role_id"), rs.getBoolean("music_segment_skipping"),
                        rs.getInt("music_default_volume"), rs.getBoolean("music_fair_queue_enabled"), rs.getInt("music_fair_queue_threshold"))
                        : new GuildSettings(guildId, 0, 0, "s!", true, false, 0, false, 100, true, 3); // default settings
            }
        }
        catch (final SQLException ex)
        {
            LOGGER.error("There was an error while requesting the guild settings for guild {}!", guildId, ex);
        }
        return null;
    }

    public static void removeGuild(final long guildId)
    {
        try (final var db = initializeConnection(); final var ps = db.prepareStatement("DELETE FROM guilds WHERE guild_id=" + guildId))
        {
            ps.executeUpdate();
        }
        catch (final SQLException ex)
        {
            LOGGER.error("There was an error while removing the entry for guild {}!", guildId, ex);
        }
    }

    // helper method

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
            LOGGER.error("There was an error while setting the {} property for guild {}!", property, guildId, ex);
        }
    }

    // guild setters

    public static void setLogChannelId(final long guildId, final long channelId)
    {
        executeSetQuery("log_channel_id", guildId, channelId);
    }

    public static void setJoinRoleId(final long guildId, final long roleId)
    {
        executeSetQuery("join_role_id", guildId, roleId);
    }

    public static void setPrefix(final long guildId, final String prefix)
    {
        executeSetQuery("prefix", guildId, prefix);
    }

    public static void setSnipingEnabled(final long guildId, final boolean enabled)
    {
        executeSetQuery("sniping", guildId, enabled);
    }

    public static void setVip(final long guildId, final boolean vip)
    {
        executeSetQuery("vip", guildId, vip);
    }

    // music setters

    public static void setDJRoleId(final long guildId, final long djRoleId)
    {
        executeSetQuery("music_dj_role_id", guildId, djRoleId);
    }

    public static void setSegmentSkippingEnabled(final long guildId, final boolean enabled)
    {
        executeSetQuery("music_segment_skipping", guildId, enabled);
    }

    public static void setDefaultVolume(final long guildId, final int defaultVolume)
    {
        executeSetQuery("music_default_volume", guildId, defaultVolume);
    }

    // fair queue setters

    public static void setFairQueueEnabled(final long guildId, final boolean enabled)
    {
        executeSetQuery("music_fair_queue_enabled", guildId, enabled);
    }

    public static void setFairQueueThreshold(final long guildId, final int threshold)
    {
        executeSetQuery("music_fair_queue_threshold", guildId, threshold);
    }
}