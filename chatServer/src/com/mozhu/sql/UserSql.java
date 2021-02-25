package com.mozhu.sql;

import java.sql.SQLException;
import java.util.ArrayList;

import com.mozhu.entity.User;

/**
 * �û���Ϣ��ѯ
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
	 * ��ѯָ���û�
	 * @param username �û���
	 * @param account �˺�
	 * @param mode ��ѯ��ʽ 0:�û��� 1:�˺� 2:�û���+�˺�
	 * @return ��ѯ�����û��б�
	 */
	public ArrayList<User> query(String username, int account, int mode) {
		ArrayList<User> r = new ArrayList<User>();
		try {
			stm = con.prepareStatement(querySql);
			switch(mode) {
			case 0://�û���
				stm.setString(1, username);
				stm.setInt(2, -1);
				break;
			case 1://�˺�
				stm.setString(1, "");
				stm.setInt(2, account);
				break;
			case 2://�û���+�˺�
				stm.setString(1, username);
				stm.setInt(2, account);
				break;
			}
			rs = stm.executeQuery();
			//�����ѯ���
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
	 * ����û�
	 * @param user �û�
	 * @return �Ƿ���ӳɹ�
	 */
	public boolean add(String nickname, String password) {
		//�����˺�
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
	 * ɾ���û�
	 * @param user �û�
	 * @return �Ƿ�ɾ���ɹ�
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
	 * �޸��û�����
	 * @param user �û�
	 * @param updateField �޸ĵ��ֶ�
	 * @param newValue �޸�ֵ
	 * @return �Ƿ��޸ĳɹ�
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
	 * �����˺�
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
