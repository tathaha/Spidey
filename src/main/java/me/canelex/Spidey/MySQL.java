package me.canelex.DiscordBOT;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {
	
	public static Connection c;	
	
	public static synchronized void createGuildTable(String guildID) {
		
		try {
			
			Class.forName("com.mysql.cj.jdbc.Driver");			
			c = DriverManager.getConnection("jdbc:mysql://" + Secrety.host + ":" + Secrety.port + "/" + Secrety.database, Secrety.username, Secrety.pass);
			PreparedStatement ps = c.prepareStatement("CREATE TABLE IF NOT EXISTS `" + guildID + "` (`ID` VARCHAR(25) not null)");
			ps.executeUpdate();
			ps.close();
			c.close();
						
		} 
		
		catch (SQLException | ClassNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
	}	
	
	public static synchronized void saveLogChannel(String guildID, String tcID) {
		
		try {
			
			Class.forName("com.mysql.cj.jdbc.Driver");			
			c = DriverManager.getConnection("jdbc:mysql://" + Secrety.host + ":" + Secrety.port + "/" + Secrety.database, Secrety.username, Secrety.pass);
			
			PreparedStatement ps = c.prepareStatement("INSERT INTO `"+ guildID +"` (`ID`) VALUES (?);");
			
			ps.setString(1, tcID);
			
			ps.executeUpdate();
			
			ps.close();
			c.close();
						
		} 
		
		catch (SQLException | ClassNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
	}	
	
	public static synchronized String getLogChannel(String guildID) {
		
		try {
				
			Class.forName("com.mysql.cj.jdbc.Driver");		
			c = DriverManager.getConnection("jdbc:mysql://" + Secrety.host + ":" + Secrety.port + "/" + Secrety.database, Secrety.username, Secrety.pass);
			
			PreparedStatement ps = c.prepareStatement("SELECT *, COUNT(*) AS total FROM `"+ guildID +"`;");
			
			ResultSet rs = ps.executeQuery();
			
			rs.next();
			
			if (rs.getInt("total") != 0) {
				
                String s = rs.getString("ID");
				rs.close();
				ps.close();
				c.close();
				return s;
				
			}
			
		} 
		
		catch (SQLException | ClassNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
		return null;
		
	}	
	
    public static synchronized void deleteLogChannel(String guildID) {
		
	    try {

		    c = DriverManager.getConnection("jdbc:mysql://" + Secrety.host + ":" + Secrety.port + "/" + Secrety.database, Secrety.username, Secrety.pass);
		
		    PreparedStatement ps = c.prepareStatement("DELETE FROM `" + guildID + "` WHERE `ID`=?;");
		    ps.setString(1, getLogChannel(guildID));
		
		    ps.executeUpdate();
			
		
		    ps.close();
		    c.close();
		
	    } 
	
	    catch (SQLException e) {
    	
    	    e.printStackTrace();
    	
        } 
	
    }	

}
