package com.mozhu.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.mozhu.net.HandleClient;

/**
 * ������
 * @author Administrator
 *����ӵ�ģ�飺
 *��Ϣ�洢 һ�����������洢500����Ϣ�����и������Ϣʱ����ǰ400����Ϣ�������ݿ⣬Ȼ������������
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
	 * ��ӳ�Ա
	 * @param member User��Ա
	 */
	public void addMember(User member, HandleClient socket) {
		isActivation = true;
		memList.put(member, socket);
		memberNum++;
		transmitHint(member.getName() + "������������");
		updateMemberList();
	}
	
	/**
	 * �Ƴ���Ա
	 * @param member User��Ա
	 */
	public void removeMember(User member) {
		memList.remove(member);
		memberNum--;
		transmitHint(member.getName() + "�˳���������");
		updateMemberList();
	}
	
	/**
	 * ����ȺԱ�б�
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
	 * ��ȡ�����ҳ�Ա�б�
	 * @return
	 */
	public Set<User> getMemberList() {
		return memList.keySet();
	}
	
	/**
	 * �������ҹ㲥��Ϣ
	 * @param msg �㲥����Ϣ
	 * @param user �㲥��Ϣ���û�
	 * ������߳������������̰߳�ȫ��
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
	 * �㲥��������ʾ
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
