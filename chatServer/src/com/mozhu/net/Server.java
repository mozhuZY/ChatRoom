package com.mozhu.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mozhu.manage.ChatRoomManager;
import com.mozhu.sql.UserSql;

/**
 * 网络服务端
 * @author Administrator
 *
 */
public class Server{
	private ServerSocket server;
	//线程池
	private ExecutorService threadPool;
	//聊天室管理器
	private ChatRoomManager manager;
	//数据库连接
	private UserSql usersql;
	//单个核心的线程池大小
	private static final int SINGLECORE_POOLSIZE = 4;
	//用户连接数
	private static int userNum;
	
	public Server(int port) {
		userNum = 0;
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("已启用端口：" + port);
		//聊天室管理器初始化
		manager = new ChatRoomManager();
		new Thread(manager).start();
		//数据库连接初始化
		usersql = new UserSql();
		//线程池初始化
		threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * SINGLECORE_POOLSIZE);
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
				threadPool.execute(thread);
				userNum++;
				System.out.println("ip：" + socket.getLocalAddress().toString() + " 已接入");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static int getUserNum() {
		return userNum;
	}
	
	public static void setUserNum(int num) {
		Server.userNum = num;
	}
}
