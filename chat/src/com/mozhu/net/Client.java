package com.mozhu.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.mozhu.entity.Message;
import com.mozhu.ui.MainScreen;
import com.mozhu.ui.MultPeopleChatScreen;

/**
 * 网络客户端
 * @author Administrator
 *
 */
public class Client extends Thread{
	private Socket socket;
	private BufferedReader read;
	private PrintWriter write;
	
	private MultPeopleChatScreen screen;
	
	private ArrayList<String> result;
	private boolean isReturned;
	private boolean exit;
	
	public Client() {}
	
	public Client(String host, int port) {
		//连接服务器
		if(Connection(host, port)) {
			try {
				//初始化网络输入输出流
				read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				write = new PrintWriter(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			//提示连接失败，请检查网络是否通畅或地址、端口号是否正确
			JOptionPane.showMessageDialog(null, "服务器连接失败\n请检查网络是否连接通畅", "错误", JOptionPane.ERROR_MESSAGE);
		}
		this.result = null;
		this.screen = null;
		this.isReturned = false;
		this.exit = false;
	}
	
	/**
	 * 连接服务器
	 * @param host 服务器地址
	 * @param port 端口号
	 * @return 是否连接成功
	 */
	public boolean Connection(String host, int port) {
		try {
			socket = new Socket(host, port);
			//初始化网络输入输出流
			read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			write = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			if(socket != null) {
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			return false;
		}
		return true;
	}
	
	/**
	 * 替换字符串
	 * @param str 替换的字符串
	 * @return 替换后的字符串
	 */
	public static String replaceString(String str) {
		String r = str;
		r = r.trim();
		r = r.replace(' ', '$');
		r = r.replace('\n', '|');
		return r;
	}
	
	/**
	 * 还原字符串
	 * @param str 还原的字符串
	 * @return 还原后的字符串
	 */
	public String recoverString(String str) {
		String r = str;
		r = r.replace('$', ' ');
		r = r.replace('|', '\n');
		return r;
	}
	
	/**
	 * 向服务器发送数据
	 * @param msg 数据
	 */
	public synchronized void send(String command) {
		System.out.println("Client:" + command);
		write.println(command);
		write.flush();
	}
	
	/**
	 * 等待服务器响应
	 * @return 服务器是否响应
	 * 
	 * 此方法已添加互斥锁
	 */
	public boolean waitReturn() {
		for(int i = 0; i < 100; i++) {
			synchronized(this) {
				if(isReturned) {
					return true;
				}
			}
			try {
				Thread.sleep(10);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 获取服务端返回的数据
	 * @return 返回的数据 由String类型表示
	 */
	public ArrayList<String> getResult() {
		synchronized(this) {
			this.isReturned = false;
		}
		if(result != null) {
			ArrayList<String> r = result;
			result = null;
			return r;
		}
		return null;
	}
	
	public void setMultChatScreen(MultPeopleChatScreen screen) {
		this.screen = screen;
	}
	
	/**
	 * 数据接收（线程方法）
	 */
	public void run() {
		String[] serverData = null;
		String t = null;
		int command = 0;
		while(!exit) {
			//接收数据
			try {
				t = read.readLine();
//				if(t == null) {
//					continue;
//				}
				serverData = t.split(" ");
				command = Integer.valueOf(serverData[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(int i = 1; i < serverData.length; i++) {
				serverData[i] = recoverString(serverData[i]);
			}
			System.out.println("Server->" + t);
			//执行命令
			switch(command) {
			case 1://接收消息
				String user, msg;
				user = serverData[1];
				msg = serverData[2];
				Message message = new Message(user, LocalDateTime.now(), msg);
				this.screen.recvMsg(message);
				break;
			case 2://聊天室提示
				if(this.screen != null) {
					this.screen.recvHint(serverData[1]);
				}
				break;
			case 3://群员列表刷新
				if(this.screen != null) {
					ArrayList<String> list = new ArrayList<String>();
					for(int i = 1; i < serverData.length; i++) {
						list.add(serverData[i]);
					}
					this.screen.updateMemberList(list);
				}
				break;
			case 4://同步消息
				String userSync = "", msgSync = "";
				ArrayList<Message> messages = new ArrayList<Message>();
				for(int i = 1;i < serverData.length;i += 2) {
					userSync = serverData[i];
					msgSync = serverData[i + 1];
					messages.add(new Message(userSync, LocalDateTime.now() , msgSync));
				}
				this.screen.getMsgList().addArrayMessage(messages);
				break;
			case 5://聊天室列表刷新
				ArrayList<String> roomlist = new ArrayList<String>();
				for(int i = 1; i < serverData.length; i++) {
					roomlist.add(serverData[i]);
				}
				MainScreen.updateChatRoomList(roomlist);
				break;
			case 0://指令执行成功，返回数据
				ArrayList<String> r = new ArrayList<String>();
				for(int i = 1; i < serverData.length; i++) {
					System.out.println(serverData[i]);
					r.add(serverData[i]);
				}
				this.result = r;
				synchronized(this) {
					this.isReturned = true;
				}
				break;
			case -1://指令执行错误
				this.result = null;
				synchronized(this) {
					this.isReturned = true;
				}
				break;
			}
		}
	}
	
	public boolean isReturned() {
		return isReturned;
	}
	
	public void exit() throws InterruptedException {
		exit = true;
		join();
	}
}
