package com.mozhu.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.mozhu.net.HandleClient;

/**
 * 聊天室
 * @author Administrator
 *待添加的模块：
 *消息存储 一个聊天室最多存储500条消息，当有更多的消息时，将前400条消息存入数据库，然后从聊天室清除
 */
public class ChatRoom {
	private Map<User, HandleClient> memList;
	private String name;
	private User manager;
	private int memberNum;
	public boolean isActivation;
	
	public ChatRoom() {
		memList = new HashMap<User, HandleClient>();
	}
	
	public ChatRoom(String name, User manager) {
		this.name = name;
		this.manager = manager;
		this.memberNum = 0;
		this.isActivation = false;
		this.memList = new HashMap<User, HandleClient>();
	}
	
	/**
	 * 添加成员
	 * @param member User成员
	 */
	public void addMember(User member, HandleClient socket) {
		isActivation = true;
		memList.put(member, socket);
		memberNum++;
		transmitHint(member.getName() + "加入了聊天室");
		updateMemberList();
	}
	
	/**
	 * 移除成员
	 * @param member User成员
	 */
	public void removeMember(User member) {
		memList.remove(member);
		memberNum--;
		transmitHint(member.getName() + "退出了聊天室");
		updateMemberList();
	}
	
	/**
	 * 更新群员列表
	 */
	public synchronized void updateMemberList() {
		String mList = "";
		Set<User> list = getMemberList();
		ArrayList<HandleClient> socketList = new ArrayList<HandleClient>();
		Iterator<User> it = list.iterator();
		while(it.hasNext()) {
			User temp = it.next();
			mList += " " + HandleClient.replaceString(temp.getName());
			socketList.add(memList.get(temp));
		}
		for(int i = 0; i < socketList.size(); i++) {
			socketList.get(i).send("3" + mList);
		}
	}
	
	/**
	 * 获取聊天室成员列表
	 * @return
	 */
	public Set<User> getMemberList() {
		return memList.keySet();
	}
	
	/**
	 * 向聊天室广播信息
	 * @param msg 广播的信息
	 * @param user 广播信息的用户
	 * 添加了线程锁，所以是线程安全的
	 */
	public synchronized void transmitMsg(Message msg, User user) {
		Iterator<User> it = memList.keySet().iterator();
		while(it.hasNext()) {
			User temp = it.next();
			if(!temp.equals(user)) {
				memList.get(temp).send("1 " + user.getName() + " " + HandleClient.replaceString(msg.getMessage()));
			}
		}
	}
	
	/**
	 * 广播聊天室提示
	 * @param hint
	 */
	public void transmitHint(String hint) {
		Iterator<User> it = memList.keySet().iterator();
		while(it.hasNext()) {
			memList.get(it.next()).send("2 " + hint);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public User getManager() {
		return manager;
	}
	
	public void setManager(User manager) {
		this.manager = manager;
	}
	
	public int getMemberNum() {
		return memberNum;
	}
}
