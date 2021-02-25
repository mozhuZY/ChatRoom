package com.mozhu.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
	//时间格式
	private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	private String username;
	private LocalDateTime time;
	private String message;
	
	public Message() {}

	public Message(String username, LocalDateTime time, String message) {
		super();
		this.username = username;
		this.time = time;
		this.message = message;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTime() {
		return time.format(format);
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
