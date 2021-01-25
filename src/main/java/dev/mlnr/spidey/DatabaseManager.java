package dev.mlnr.spidey;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.mlnr.spidey.objects.guild.GuildSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class DatabaseManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);
    private static final HikariDataSource HIKARI_DATA_SOURCE;

    private DatabaseManager() {}

    static
    {
        var hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setJdbcUrl("jdbc:postgresql:spidey");
        hikariConfig.setUsername("sebo");
        hikariConfig.setPassword(System.getenv("db"));
        HIKARI_DATA_SOURCE = new HikariDataSource(hikariConfig);
    }

    public static GuildSettings retrieveGuildSettings(long guildId)
    {
        try (var con = HIKARI_DATA_SOURCE.getConnection(); var ps = con.prepareStatement("SELECT * FROM guilds WHERE guild_id=?"))
        {
            ps.setLong(1, guildId);
            try (var rs = ps.executeQuery())
            {
                return rs.next() ? new GuildSettings(guildId, rs.getLong("log_channel_id"), rs.getLong("join_role_id"), rs.getString("prefix"), rs.getString("language"),
                        rs.getBoolean("sniping_enabled"), rs.getBoolean("error_cleanup_enabled"), rs.getBoolean("vip"), rs.getLong("music_dj_role_id"),
                        rs.getBoolean("music_segment_skipping"), rs.getInt("music_default_volume"), rs.getBoolean("music_fair_queue_enabled"),
                        rs.getInt("music_fair_queue_threshold"))
                        : new GuildSettings(guildId, 0, 0, "s!", "en", true, false, false, 0, false, 100, true, 3); // default settings
            }
        }
        catch (SQLException ex)
        {
            LOGGER.error("There was an error while requesting the guild settings for guild {}!", guildId, ex);
        }
        return null;
    }

    public static void removeGuild(long guildId)
    {
        try (var con = HIKARI_DATA_SOURCE.getConnection(); var ps = con.prepareStatement("DELETE FROM guilds WHERE guild_id=" + guildId))
        {
            ps.executeUpdate();
        }
        catch (SQLException ex)
        {
            LOGGER.error("There was an error while removing the entry for guild {}!", guildId, ex);
        }
    }

    // helper method

    private static <T> void executeSetQuery(String property, long guildId, T value)
    {
        var query = "INSERT INTO guilds (guild_id, " + property + ") VALUES (?, ?) ON CONFLICT (guild_id) DO UPDATE SET " + property + "='" + value + "'";
        try (var con = HIKARI_DATA_SOURCE.getConnection(); var ps = con.prepareStatement(query))
        {
            ps.setLong(1, guildId);
            ps.setObject(2, value);
            ps.executeUpdate();
        }
        catch (SQLException ex)
        {
            LOGGER.error("There was an error while setting the {} property for guild {}!", property, guildId, ex);
        }
    }

    // guild setters

    public static void setLogChannelId(long guildId, long channelId)
    {
        executeSetQuery("log_channel_id", guildId, channelId);
    }

    public static void setJoinRoleId(long guildId, long roleId)
    {
        executeSetQuery("join_role_id", guildId, roleId);
    }

    public static void setPrefix(long guildId, String prefix)
    {
        executeSetQuery("prefix", guildId, prefix);
    }

    public static void setLanguage(long guildId, String language)
    {
        executeSetQuery("language", guildId, language);
    }

    public static void setSnipingEnabled(long guildId, boolean enabled)
    {
        executeSetQuery("sniping_enabled", guildId, enabled);
    }

    public static void setErrorCleanupEnabled(long guildId, boolean enabled)
    {
        executeSetQuery("error_cleanup_enabled", guildId, enabled);
    }

    public static void setVip(long guildId, boolean vip)
    {
        executeSetQuery("vip", guildId, vip);
    }

    // music setters

    public static void setDJRoleId(long guildId, long djRoleId)
    {
        executeSetQuery("music_dj_role_id", guildId, djRoleId);
    }

    public static void setSegmentSkippingEnabled(long guildId, boolean enabled)
    {
        executeSetQuery("music_segment_skipping", guildId, enabled);
    }

    public static void setDefaultVolume(long guildId, int defaultVolume)
    {
        executeSetQuery("music_default_volume", guildId, defaultVolume);
    }

    // fair queue setters

    public static void setFairQueueEnabled(long guildId, boolean enabled)
    {
        executeSetQuery("music_fair_queue_enabled", guildId, enabled);
    }

    public static void setFairQueueThreshold(long guildId, int threshold)
    {
        executeSetQuery("music_fair_queue_threshold", guildId, threshold);
    }
}