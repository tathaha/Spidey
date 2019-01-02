package me.canelex.Spidey;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {
	
	private static Connection c;		
	
	public static synchronized Long getChannelId(Long serverId) {
		
		try {
			
			c = DriverManager.getConnection("jdbc:mysql://" + Secrets.host + ":" + Secrets.port + "/" + Secrets.database, Secrets.username, Secrets.pass);
			PreparedStatement ps = c.prepareStatement("SELECT *, COUNT(*) AS total FROM `servers` WHERE `server_id`=? LIMIT 1;");
			ps.setLong(1, serverId);
			ResultSet rs = ps.executeQuery();
			rs.next();
			
			if (rs.getInt("total") != 0) {
				
				Long l = rs.getLong("channel_id");
				rs.close();
				ps.close();
				c.close();
				return l;
				
			}
			
		} 
		
		catch (SQLException e) {
			
			e.printStackTrace();
			
		}
		
		return null;
		
	}
	
	public static synchronized void insertData(Long serverId, Long channelId) {
		
		try {
			
			c = DriverManager.getConnection("jdbc:mysql://" + Secrets.host + ":" + Secrets.port + "/" + Secrets.database, Secrets.username, Secrets.pass);
			PreparedStatement ps = c.prepareStatement("INSERT INTO `servers` (`server_id`, `channel_id`) VALUES (?, ?);");
			ps.setLong(1, serverId);
			ps.setLong(2, channelId);
			ps.executeUpdate();
			ps.close();
			c.close();
			
		} 
		
		catch (SQLException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public static synchronized void removeData(Long serverId) {
		
		try {
			
			c = DriverManager.getConnection("jdbc:mysql://" + Secrets.host + ":" + Secrets.port + "/" + Secrets.database, Secrets.username, Secrets.pass);
			PreparedStatement ps = c.prepareStatement("DELETE * FROM `servers` WHERE `server_id`=?;");
			ps.setLong(1, serverId);
			ps.executeUpdate();
			ps.close();
			c.close();
			
		} 
		
		catch (SQLException e) {
			
			e.printStackTrace();
			
		}
		
	}	
	
	public static synchronized boolean isInDatabase(Long serverId) {
		try {
			
			c = DriverManager.getConnection("jdbc:mysql://" + Secrets.host + ":" + Secrets.port + "/" + Secrets.database, Secrets.username, Secrets.pass);
			PreparedStatement ps = c.prepareStatement("SELECT *, COUNT(*) AS total FROM `servers` WHERE `server_id`=? LIMIT 1;");
			ps.setLong(1, serverId);
			ResultSet rs = ps.executeQuery();
			rs.next();
			if (rs.getInt("total") != 0) {
				
				rs.close();
				ps.close();
				c.close();
				return true;
				
			}
			
		} 
		
		catch (SQLException e) {
			
			e.printStackTrace();
			
		}
				
		return false;
		
	}	

}
