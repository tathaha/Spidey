package me.canelex.spidey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("SqlNoDataSourceInspection")
public class MySQL {

	private static Connection c;

	private MySQL() { super(); }

	private static final Logger logger = LoggerFactory.getLogger(MySQL.class);

	public static synchronized Long getChannelId(final Long serverId) {
		try {
			c = DriverManager.getConnection("jdbc:mysql://" + Secrets.HOST + ":" + Secrets.PORT + "/" + Secrets.DATABASE, Secrets.USERNAME, Secrets.PASS);
			final var ps = c.prepareStatement("SELECT *, COUNT(*) AS total FROM `servers` WHERE `server_id`=? LIMIT 1;");
			ps.setLong(1, serverId);
			final var rs = ps.executeQuery();
			rs.next();
			if (rs.getInt("total") != 0) {
				final Long l = rs.getLong("channel_id");
				rs.close();
				ps.close();
				c.close();
				return l;
			}
		}
		catch (final SQLException e) {
			logger.error("Exception!", e);
		}
		return null;
	}

	public static synchronized void insertData(final Long serverId, final Long channelId) {
		try {
			c = DriverManager.getConnection("jdbc:mysql://" + Secrets.HOST + ":" + Secrets.PORT + "/" + Secrets.DATABASE, Secrets.USERNAME, Secrets.PASS);
			final var ps = c.prepareStatement("INSERT INTO `servers` (`server_id`, `channel_id`) VALUES (?, ?);");
			ps.setLong(1, serverId);
			ps.setLong(2, channelId);
			ps.executeUpdate();
			ps.close();
			c.close();
		}
		catch (final SQLException e) {
			logger.error("Exception!", e);
		}
	}

	public static synchronized void removeData(final Long serverId) {
		try {

			c = DriverManager.getConnection("jdbc:mysql://" + Secrets.HOST + ":" + Secrets.PORT + "/" + Secrets.DATABASE, Secrets.USERNAME, Secrets.PASS);
			final var ps = c.prepareStatement("DELETE FROM `servers` WHERE `server_id`=?;");
			ps.setLong(1, serverId);
			ps.executeUpdate();
			ps.close();
			c.close();
		}

		catch (final SQLException e) {
			logger.error("Exception!", e);
		}
	}
}
