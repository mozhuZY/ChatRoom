package com.mozhu.entity;

public class User {
	int account;
	String name;
	String password;
	
	public User() {}
	
	public User(int account, String name, String password) {
		this.account = account;
		this.name = name;
		this.password = password;
	}
	
	public int getAccount() {
		return account;
	}

	public void setAccount(int account) {
		this.account = account;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
