package com.mozhu.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库类
 * @author Administrator
 * 提供统一的数据库连接
 */
public class Sql {
	protected Connection con;
	protected PreparedStatement stm;
	protected ResultSet rs;
	
	private static String url = "jdbc:mysql://localhost:3306/user";
	private static String username = "root";
	private static String password = "123456";
	
	public Sql() {
		//连接数据库
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password);
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
