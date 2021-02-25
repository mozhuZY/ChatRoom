package com.mozhu.manage;

import java.util.ArrayList;

import com.mozhu.entity.ChatRoom;
import com.mozhu.entity.User;
import com.mozhu.net.HandleClient;

public class ChatRoomManager implements Runnable{
	private static ArrayList<ChatRoom> roomList;
	
	public ChatRoomManager() {
		roomList = new ArrayList<ChatRoom>();
	}
	
	/**
	 * ���������
	 * @param room �����Ҷ���
	 */
	public void addChatRoom(ChatRoom room) {
		roomList.add(room);
	}
	
	/**
	 * �Ƴ�������
	 * @param room �����Ҷ���
	 */
	public void deleteChatRoom(ChatRoom room) {
		roomList.remove(room);
	}
	
	/**
	 * ��ȡ������
	 * @param roomName ����������
	 * @return ����һ�������Ҷ������������б���û���������ͬ���������ң��򷵻�null
	 */
	public ChatRoom getChatRoom(String roomName) {
		for(int i = 0;i < roomList.size();i++) {
			if(roomList.get(i).getName().equals(roomName)) {
				return roomList.get(i);
			}
		}
		return null;
	}
	
	/**
	 * ��ȡ�����������б�
	 * @return
	 */
	public ArrayList<String> getChatRoomNameList(){
		ArrayList<String> r = new ArrayList<String>();
		for(int i = 0;i < roomList.size();i++) {
			r.add(roomList.get(i).getName());
		}
		return r;
	}
	
	/**
	 * ����������
	 * @param user �û�
	 * @param socket ���Ӵ�����
	 * @param chatRoomName ����������
	 * @return ���������Ҷ��� ���������б�û�ж�Ӧ���������򷵻�null
	 */
	public ChatRoom inChatRoom(User user, HandleClient socket, String chatRoomName) {
		for(int i = 0; i < roomList.size(); i++) {
			ChatRoom temp = roomList.get(i);
			if(temp.getName().equals(chatRoomName)){
				roomList.get(i).addMember(user, socket);
				return temp;
			}
		}
		return null;
	}
	
	/**
	 * �̷߳���
	 * ������������Ҳ�ִ��������ָ��
	 */
	@Override
	public void run() {
		while(true) {
			synchronized(this) {
				//���������
				for(int i = 0; i < roomList.size(); i++) {
					ChatRoom temp = roomList.get(i);
					if(temp.getMemberNum() == 0) {
						if(temp.isActivation) {
							deleteChatRoom(temp);
							System.out.println("ChatRoom \"" + temp.getName() + "\"has been closed");
						}
					}
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
