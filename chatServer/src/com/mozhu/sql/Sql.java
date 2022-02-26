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
	
	private static String url = "jdbc:mysql://localhost:3306/chat";
	private static String username = "root";
	private static String password = "147258369asd";
	
	public Sql() {
		//连接数据库
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password);
		}catch(ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}
