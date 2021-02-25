package com.mozhu.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.mozhu.manage.ChatRoomManager;
import com.mozhu.sql.UserSql;

/**
 * ��������
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
		System.out.println("�����ö˿ڣ�" + port);
		manager = new ChatRoomManager();
		new Thread(manager).start();
		usersql = new UserSql();
	}
	
	/**
	 * ����ͻ�������
	 */
	public void run() {
		Socket socket = null;
		Thread thread = null;
		//Ԥ��HandleClient��
		HandleClient.manager = manager;
		HandleClient.usersql = usersql;
		System.out.println("���ڵȴ�����...");
		while(true) {
			try {
				socket = server.accept();
				thread = new HandleClient(socket);
				thread.start();
				System.out.println("ip��" + socket.getLocalAddress().toString() + " �ѽ���");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
