package com.mozhu.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mozhu.manage.ChatRoomManager;
import com.mozhu.sql.UserSql;

/**
 * ��������
 * @author Administrator
 *
 */
public class Server{
	private ServerSocket server;
	//�̳߳�
	private ExecutorService threadPool;
	//�����ҹ�����
	private ChatRoomManager manager;
	//���ݿ�����
	private UserSql usersql;
	//�������ĵ��̳߳ش�С
	private static final int SINGLECORE_POOLSIZE = 4;
	//�û�������
	private static int userNum;
	
	public Server(int port) {
		userNum = 0;
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("�����ö˿ڣ�" + port);
		//�����ҹ�������ʼ��
		manager = new ChatRoomManager();
		new Thread(manager).start();
		//���ݿ����ӳ�ʼ��
		usersql = new UserSql();
		//�̳߳س�ʼ��
		threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * SINGLECORE_POOLSIZE);
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
				threadPool.execute(thread);
				userNum++;
				System.out.println("ip��" + socket.getLocalAddress().toString() + " �ѽ���");
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
