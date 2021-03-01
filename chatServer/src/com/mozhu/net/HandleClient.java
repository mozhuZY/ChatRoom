package com.mozhu.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.mozhu.entity.ChatRoom;
import com.mozhu.entity.Message;
import com.mozhu.entity.User;
import com.mozhu.manage.ChatRoomManager;
import com.mozhu.sql.UserSql;

/**
 * 处理客户端连接
 * @author Administrator
 *
 */
public class HandleClient extends Thread{
	public static ChatRoomManager manager;
	public static UserSql usersql;
	
	private Socket socket;
	private BufferedReader read;
	private PrintWriter write;
	private Boolean isExit;
	private ChatRoom room;
	private User user;
	
	public HandleClient(Socket socket) {
		this.socket = socket;
		try {
			read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			write = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		}catch(IOException e) {
			e.printStackTrace();
		}
		isExit = false;
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
	 * 向客户端发送数据
	 * @param command 指令
	 */
	public void send(String command) {
		System.out.println("Server:" + command);
		write.println(command);
		write.flush();
	}
	
	/**
	 * 处理客户端信数据
	 */
	@Override
	public void run() {
		String[] clientData = null;
		String t = null;
		int command = 0;
		while(!isExit) {
			//获取客户端信息
			try {
				t = read.readLine();
				if(t == null) {
					continue;
				}
				clientData = t.split(" ");
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(getAddress() + "->" + t);
			//处理客户端指令
			for(int i = 1; i < clientData.length; i++) {
				clientData[i] = recoverString(clientData[i]);
			}
			command = Integer.valueOf(clientData[0]); 
			switch(command) {
			case 1://登录
				//连接数据库,查询账户信息
				ArrayList<User> l = usersql.query("", Integer.valueOf(clientData[1]), 1);
				if(l.size() != 0) {
					User temp = l.get(0);
					if(temp.getPassword().equals(clientData[2])) {
						user = temp;
						send("0 " + user.getAccount() + " " + user.getName() + " " + user.getPassword());
					} else {
						send("-1");
					}
				} else {
					send("-1");
				}
				break;
			case 2://进入聊天室
				String chatRoomName = clientData[1];
				room = manager.inChatRoom(user, this, chatRoomName);
				if(room == null) {
					send("-1");
				}else {
					send("0 " + replaceString(room.getName()));
				}
				break;
			case 3://发送聊天室消息
				Message msg = new Message(user.getName(), LocalDateTime.now(), clientData[1]);
				room.transmitMsg(msg, user);
				break;
			case 4://退出聊天室
				room.removeMember(user);
				room = null;
				break;
			case 5://创建聊天室
				if(Integer.valueOf(clientData[2]) == 1) {
					manager.addChatRoom(new ChatRoom(clientData[1], user, false));
					System.out.println("动态聊天室" + "\"" + clientData[1] + "\"已被" + user.getName() + "(" + socket.getInetAddress().toString() + ")创建");
				} else {
					manager.addChatRoom(new ChatRoom(clientData[1], user, true));
					System.out.println("静态聊天室" + "\"" + clientData[1] + "\"已被" + user.getName() + "(" + socket.getInetAddress().toString() + ")创建");
				}
				
				break;
			case 6://刷新聊天室列表
				ArrayList<String> roomlist = manager.getChatRoomNameList();
				String sendCommand = "5 ";
				for(int i = 0; i < roomlist.size(); i++) {
					sendCommand += " " + replaceString(roomlist.get(i));
				}
				send(sendCommand);
				break;
			case 7://刷新群员列表
				this.room.updateMemberList();
				break;
			case 8://注册账号
				if(usersql.add(clientData[1], clientData[2])) {
					ArrayList<User> rUser = usersql.query(clientData[1], 0, 0);
					send("0 " + rUser.get(0).getAccount());
				} else {
					send("-1");
				}
				
				break;
			case 9://查询账号
				ArrayList<User> qUser = usersql.query(clientData[1], Integer.valueOf(clientData[2]), Integer.valueOf(clientData[3]));
				if(qUser.size() != 0) {
					User u = qUser.get(0);
					send("0 " + u.getAccount() + " " + u.getName());
				} else {
					send("-1");
				}
				break;
			case 0://断开连接
				System.out.println("(" + socket.getInetAddress().toString() + ")已断开连接");
				isExit = true;
				
				break;
			}
		}
		try {
			if(socket != null) {
				socket.close();
			}
			if(read != null) {
				read.close();
			}
			if(write != null) {
				write.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Server.setUserNum(Server.getUserNum() - 1);
	}
	
	/**
	 * 获取客户端地址
	 * @return
	 */
	public String getAddress() {
		return socket.getInetAddress().toString();
	}
}
