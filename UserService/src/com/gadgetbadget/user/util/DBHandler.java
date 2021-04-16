package com.gadgetbadget.user.util;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * This class acts as a Super class for all classes where database access is needed. The accurate 
 * connection strings and a method for obtaining a database connection via JDBC driver are already
 * well configured in a way all the sub classes can easily use them by extending this class. Highly 
 * effective when the connection string/port or the host are changing rapidly/ often. Developers can
 * easily update this class which fixes all connection issues for all the extending classes.
 * 
 * @author Ishara_Dissanayake
 */
public class DBHandler {
	private static final String host = "127.0.0.1";
	private static final String port = "3306";
	private static final String database = "gadgetbadget_users";
	private static final String username = "root";
	private static final String password = "";
	private Connection conn = null;
	
	/**
	 * This method returns a usable JDBC database connection to be used by sub classes in the service/project.
	 * 
	 * @return returns a valid SQL connection based on the given connection string
	 */
	public Connection getConnection()
	{
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database, username, password);
		}
		catch (Exception e)
		{e.printStackTrace();}
		return conn;
	}
    
}
