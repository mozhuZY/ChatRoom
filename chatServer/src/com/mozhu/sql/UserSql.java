package com.mozhu.sql;

import java.sql.SQLException;
import java.util.ArrayList;

import com.mozhu.entity.User;

/**
 * 用户信息查询
 * @author Administrator
 *
 */
public class UserSql extends Sql{
	private static String querySql = "select * from userinfo where nickname = ? or username = ?";
	private static String insertSql = "insert into userinfo values(?,?,?)";
	private static String updateSql = "update userinfo set ? = ? where username = ?";
	private static String deleteSql = "delete from userninfo where username = ?";
	
	public UserSql() {
		
	}
	
	/**
	 * 查询指定用户
	 * @param username 用户名
	 * @param account 账号
	 * @param mode 查询方式 0:用户名 1:账号 2:用户名+账号
	 * @return 查询到的用户列表
	 */
	public ArrayList<User> query(String username, int account, int mode) {
		ArrayList<User> r = new ArrayList<User>();
		try {
			stm = con.prepareStatement(querySql);
			switch(mode) {
			case 0://用户名
				stm.setString(1, username);
				stm.setInt(2, -1);
				break;
			case 1://账号
				stm.setString(1, "");
				stm.setInt(2, account);
				break;
			case 2://用户名+账号
				stm.setString(1, username);
				stm.setInt(2, account);
				break;
			}
			rs = stm.executeQuery();
			//保存查询结果
			while(rs.next()) {
				System.out.println("queryed account: " + rs.getInt(1) + " " + rs.getString(3) + " " +rs.getString(2));
				r.add(new User(rs.getInt(1),rs.getString(3),rs.getString(2)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return r;
	}
	
	/**
	 * 添加用户
	 * @param user 用户
	 * @return 是否添加成功
	 */
	public boolean add(String nickname, String password) {
		//生成账号
		int account = generateAccount();
		if(account == 0) {
			return false;
		}
		try {
			stm = con.prepareStatement(insertSql);
			stm.setInt(1, account);
			stm.setString(2, password);
			stm.setString(3, nickname);
			if(stm.executeUpdate() == 0) {
				return false;
			}
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 删除用户
	 * @param user 用户
	 * @return 是否删除成功
	 */
	public boolean delete(User user) {
		try {
			stm = con.prepareStatement(deleteSql);
			stm.setInt(1, user.getAccount());
			if(stm.executeUpdate() == 0) {
				return false;
			}
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 修改用户数据
	 * @param user 用户
	 * @param updateField 修改的字段
	 * @param newValue 修改值
	 * @return 是否修改成功
	 */
	public boolean update(User user, String updateField, String newValue) {
		try {
			stm = con.prepareStatement(updateSql);
			stm.setString(1, updateField);
			stm.setString(2, newValue);
			stm.setInt(3, user.getAccount());
			if(stm.executeUpdate() == 0) {
				return false;
			}
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 生成账号
	 * @return
	 */
	private int generateAccount() {
		int account = 0;
		try {
			stm = con.prepareStatement("select max(username) from userinfo");
			rs = stm.executeQuery();
			if(rs.next()) {
				account = rs.getInt(1);
			}
				
		}catch(SQLException e) {
			e.printStackTrace();;
		}
		return account + 1;
	}
}
