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
 * ����ͻ�������
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
	 * �滻�ַ���
	 * @param str �滻���ַ���
	 * @return �滻����ַ���
	 */
	public static String replaceString(String str) {
		String r = str;
		r = r.trim();
		r = r.replace(' ', '$');
		r = r.replace('\n', '|');
		return r;
	}
	
	/**
	 * ��ԭ�ַ���
	 * @param str ��ԭ���ַ���
	 * @return ��ԭ����ַ���
	 */
	public String recoverString(String str) {
		String r = str;
		r = r.replace('$', ' ');
		r = r.replace('|', '\n');
		return r;
	}
	
	/**
	 * ��ͻ��˷�������
	 * @param command ָ��
	 */
	public void send(String command) {
		System.out.println("Server:" + command);
		write.println(command);
		write.flush();
	}
	
	/**
	 * ����ͻ���������
	 */
	@Override
	public void run() {
		String[] clientData = null;
		String t = null;
		int command = 0;
		while(!isExit) {
			//��ȡ�ͻ�����Ϣ
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
			//����ͻ���ָ��
			for(int i = 1; i < clientData.length; i++) {
				clientData[i] = recoverString(clientData[i]);
			}
			command = Integer.valueOf(clientData[0]); 
			switch(command) {
			case 1://��¼
				//�������ݿ�,��ѯ�˻���Ϣ
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
			case 2://����������
				String chatRoomName = clientData[1];
				room = manager.inChatRoom(user, this, chatRoomName);
				if(room == null) {
					send("-1");
				}else {
					send("0 " + replaceString(room.getName()));
				}
				break;
			case 3://������������Ϣ
				Message msg = new Message(user.getName(), LocalDateTime.now(), clientData[1]);
				room.transmitMsg(msg, user);
				break;
			case 4://�˳�������
				room.removeMember(user);
				room = null;
				break;
			case 5://����������
				manager.addChatRoom(new ChatRoom(clientData[1], user));
				System.out.println("������" + "\"" + clientData[1] + "\"�ѱ�" + user.getName() + "(" + socket.getInetAddress().toString() + ")����");
				break;
			case 6://ˢ���������б�
				ArrayList<String> roomlist = manager.getChatRoomNameList();
				String sendCommand = "5 ";
				for(int i = 0; i < roomlist.size(); i++) {
					sendCommand += " " + replaceString(roomlist.get(i));
				}
				send(sendCommand);
				break;
			case 7://ˢ��ȺԱ�б�
				this.room.updateMemberList();
				break;
			case 8://ע���˺�
				if(usersql.add(clientData[1], clientData[2])) {
					ArrayList<User> rUser = usersql.query(clientData[1], 0, 0);
					send("0 " + rUser.get(0).getAccount());
				} else {
					send("-1");
				}
				
				break;
			case 9://��ѯ�˺�
				ArrayList<User> qUser = usersql.query(clientData[1], Integer.valueOf(clientData[2]), Integer.valueOf(clientData[3]));
				if(qUser.size() != 0) {
					User u = qUser.get(0);
					send("0 " + u.getAccount() + " " + u.getName());
				} else {
					send("-1");
				}
				break;
			case 0://�Ͽ�����
				System.out.println("(" + socket.getInetAddress().toString() + ")�ѶϿ�����");
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
	 * ��ȡ�ͻ��˵�ַ
	 * @return
	 */
	public String getAddress() {
		return socket.getInetAddress().toString();
	}
}
