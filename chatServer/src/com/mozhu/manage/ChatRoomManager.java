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
	 * 添加聊天室
	 * @param room 聊天室对象
	 */
	public void addChatRoom(ChatRoom room) {
		roomList.add(room);
	}
	
	/**
	 * 移除聊天室
	 * @param room 聊天室对象
	 */
	public void deleteChatRoom(ChatRoom room) {
		roomList.remove(room);
	}
	
	/**
	 * 获取聊天室
	 * @param roomName 聊天室名称
	 * @return 返回一个聊天室对象，若聊天室列表内没有与此名称同名的聊天室，则返回null
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
	 * 获取聊天室名称列表
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
	 * 进入聊天室
	 * @param user 用户
	 * @param socket 连接处理器
	 * @param chatRoomName 聊天室名称
	 * @return 返回聊天室对象， 若聊天室列表没有对应的聊天室则返回null
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
	 * 线程方法
	 * 持续检测聊天室并执行聊天室指令
	 */
	@Override
	public void run() {
		while(true) {
			synchronized(this) {
				//检测聊天室
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
