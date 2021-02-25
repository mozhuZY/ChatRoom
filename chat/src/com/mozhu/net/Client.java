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
 * ����ͻ���
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
		//���ӷ�����
		if(Connection(host, port)) {
			try {
				//��ʼ���������������
				read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				write = new PrintWriter(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			//��ʾ����ʧ�ܣ����������Ƿ�ͨ�����ַ���˿ں��Ƿ���ȷ
			JOptionPane.showMessageDialog(null, "����������ʧ��\n���������Ƿ�����ͨ��", "����", JOptionPane.ERROR_MESSAGE);
		}
		this.result = null;
		this.screen = null;
		this.isReturned = false;
		this.exit = false;
	}
	
	/**
	 * ���ӷ�����
	 * @param host ��������ַ
	 * @param port �˿ں�
	 * @return �Ƿ����ӳɹ�
	 */
	public boolean Connection(String host, int port) {
		try {
			socket = new Socket(host, port);
			//��ʼ���������������
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
	 * ���������������
	 * @param msg ����
	 */
	public synchronized void send(String command) {
		System.out.println("Client:" + command);
		write.println(command);
		write.flush();
	}
	
	/**
	 * �ȴ���������Ӧ
	 * @return �������Ƿ���Ӧ
	 * 
	 * �˷�������ӻ�����
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
	 * ��ȡ����˷��ص�����
	 * @return ���ص����� ��String���ͱ�ʾ
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
	 * ���ݽ��գ��̷߳�����
	 */
	public void run() {
		String[] serverData = null;
		String t = null;
		int command = 0;
		while(!exit) {
			//��������
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
			//ִ������
			switch(command) {
			case 1://������Ϣ
				String user, msg;
				user = serverData[1];
				msg = serverData[2];
				Message message = new Message(user, LocalDateTime.now(), msg);
				this.screen.recvMsg(message);
				break;
			case 2://��������ʾ
				if(this.screen != null) {
					this.screen.recvHint(serverData[1]);
				}
				break;
			case 3://ȺԱ�б�ˢ��
				if(this.screen != null) {
					ArrayList<String> list = new ArrayList<String>();
					for(int i = 1; i < serverData.length; i++) {
						list.add(serverData[i]);
					}
					this.screen.updateMemberList(list);
				}
				break;
			case 4://ͬ����Ϣ
				String userSync = "", msgSync = "";
				ArrayList<Message> messages = new ArrayList<Message>();
				for(int i = 1;i < serverData.length;i += 2) {
					userSync = serverData[i];
					msgSync = serverData[i + 1];
					messages.add(new Message(userSync, LocalDateTime.now() , msgSync));
				}
				this.screen.getMsgList().addArrayMessage(messages);
				break;
			case 5://�������б�ˢ��
				ArrayList<String> roomlist = new ArrayList<String>();
				for(int i = 1; i < serverData.length; i++) {
					roomlist.add(serverData[i]);
				}
				MainScreen.updateChatRoomList(roomlist);
				break;
			case 0://ָ��ִ�гɹ�����������
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
			case -1://ָ��ִ�д���
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
