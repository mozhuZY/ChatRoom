package com.mozhu.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.mozhu.manage.ChatRoomManager;
import com.mozhu.sql.UserSql;

/**
 * 网络服务端
 * @author Administrator
 *
 */
public class Server{
	private ServerSocket server;
	
	private ChatRoomManager manager;
	private UserSql usersql;
	
	public Server(int port) {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("已启用端口：" + port);
		manager = new ChatRoomManager();
		new Thread(manager).start();
		usersql = new UserSql();
	}
	
	/**
	 * 处理客户端连接
	 */
	public void run() {
		Socket socket = null;
		Thread thread = null;
		//预设HandleClient类
		HandleClient.manager = manager;
		HandleClient.usersql = usersql;
		System.out.println("正在等待连接...");
		while(true) {
			try {
				socket = server.accept();
				thread = new HandleClient(socket);
				thread.start();
				System.out.println("ip：" + socket.getLocalAddress().toString() + " 已接入");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
