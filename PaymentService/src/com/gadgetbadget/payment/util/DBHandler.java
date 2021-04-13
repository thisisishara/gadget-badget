package com.gadgetbadget.payment.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBHandler {
	private static String host = "127.0.0.1";
	private static String port = "3306";
	private static String database = "gadgetbadget_payments";
	private static String username = "root";
	private static String password = "";
	private Connection conn = null;
	
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
