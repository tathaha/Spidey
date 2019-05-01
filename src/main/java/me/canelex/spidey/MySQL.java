package me.canelex.spidey;

import org.slf4j.LoggerFactory;

import java.sql.*;

public class MySQL {

	private static Connection c;

	private MySQL(){
		super();
	}

	public static synchronized Long getChannelId(long serverId) {

		ResultSet rs = null;
		try (PreparedStatement ps = c.prepareStatement("SELECT *, COUNT(*) AS total FROM `servers` WHERE `server_id`=? LIMIT 1;")){

			c = DriverManager.getConnection("jdbc:mysql://" + Secrets.HOST + ":" + Secrets.PORT + "/" + Secrets.DATABASE, Secrets.USERNAME, Secrets.PASS);
			ps.setLong(1, serverId);
			rs = ps.executeQuery();
			rs.next();

			if (rs.getInt("total") != 0) {

				final long l = rs.getLong("channel_id");
				c.close();
				return l;

			}

		}

		catch (final SQLException ex) {
			LoggerFactory.getLogger(MySQL.class).error("Exception!", ex);
		}
		finally {
			try {
				assert rs != null;
				rs.close();
			} catch (SQLException e) {
				LoggerFactory.getLogger(MySQL.class).error("Exception!", e);
			}
		}

		return null;

	}

	public static synchronized void insertData(Long serverId, Long channelId) {

		try (PreparedStatement ps = c.prepareStatement("INSERT INTO `servers` (`server_id`, `channel_id`) VALUES (?, ?);")){

			c = DriverManager.getConnection("jdbc:mysql://" + Secrets.HOST + ":" + Secrets.PORT + "/" + Secrets.DATABASE, Secrets.USERNAME, Secrets.PASS);
			ps.setLong(1, serverId);
			ps.setLong(2, channelId);
			ps.executeUpdate();
			c.close();

		}

		catch (final SQLException ex) {
			LoggerFactory.getLogger(MySQL.class).error("Exception!", ex);
		}

	}

	public static synchronized void removeData(Long serverId) {

		try (PreparedStatement ps = c.prepareStatement("DELETE FROM `servers` WHERE `server_id`=?;")){

			c = DriverManager.getConnection("jdbc:mysql://" + Secrets.HOST + ":" + Secrets.PORT + "/" + Secrets.DATABASE, Secrets.USERNAME, Secrets.PASS);
			ps.setLong(1, serverId);
			ps.executeUpdate();
			c.close();

		}

		catch (final SQLException ex) {
			LoggerFactory.getLogger(MySQL.class).error("Exception!", ex);
		}

	}

}
