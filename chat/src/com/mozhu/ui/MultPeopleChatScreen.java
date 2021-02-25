package com.mozhu.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mozhu.entity.Message;
import com.mozhu.entity.User;
import com.mozhu.net.Client;

/**
 * 多人聊天室界面
 * @author Administrator
 *
 */
public class MultPeopleChatScreen extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JPanel bind, bindUp, sendMsgPanel, memberPanel;
	private JList<String> memList;
	private JScrollPane msgJsp, memJsp, sendMsgJsp;
	private JTextArea message;
	private JLabel memberL;
	private JButton sendMsg;
	
	private Client client;
	private MessageList msgList;
	private User user;
	
	public MultPeopleChatScreen(String title, User user, Client client) {
		this.client = client;
		this.user = user;
		//初始化组件
		initComponent();
		
		//窗口设置
		this.setBounds(200, 200, 720, 560);
		this.setTitle(title);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				client.send("4");
				MultPeopleChatScreen.this.dispose();
			}
		});
		this.setVisible(true);
	}
	
	private void initComponent() {
		//面板
		bind = new JPanel(new GridLayout(2, 1, 0, 0));
		bindUp = new JPanel(new GridLayout(1, 2, 0, 0));
		sendMsgPanel = new JPanel(new BorderLayout());
		memberPanel = new JPanel(new BorderLayout());
		//信息列表
		msgList = new MessageList();
		msgJsp = new JScrollPane(msgList);
		//列表框
		memList = new JList<String>();
		memJsp = new JScrollPane(memList);
		//文本区域
		message = new JTextArea(6,20);
		sendMsgJsp = new JScrollPane(message);
		//标签
		memberL = new JLabel("聊天室成员");
		//按钮
		sendMsg = new JButton("发送");
		
		//组件设置
		msgList.setBounds(10, 10, this.getWidth() / 2 - 10, this.getHeight() / 2 - 10);
		msgList.setBackground(Color.white);
		memList.setVisibleRowCount(10);
		sendMsg.setActionCommand("sendMsg");
		sendMsg.addActionListener(this);
		
		//绑定组件
		memberPanel.add(memberL, BorderLayout.NORTH);
		memberPanel.add(memJsp, BorderLayout.CENTER);
		sendMsgPanel.add(sendMsgJsp, BorderLayout.CENTER);
		sendMsgPanel.add(sendMsg, BorderLayout.SOUTH);
		bindUp.add(msgJsp);
		bindUp.add(memberPanel);
		bind.add(bindUp);
		bind.add(sendMsgPanel);
		this.add(bind);
	}
	
	/**
	 * 获取消息列表
	 * @return 消息列表 MessageList
	 */
	public MessageList getMsgList() {
		return msgList;
	}
	
	/**
	 * 获取群员列表
	 * @return 群员列表 ArrayList<String>
	 */
	public ArrayList<String> getMemberList() {
		ArrayList<String> rList = new ArrayList<String>();
		for(int i = 0; i < memList.getModel().getSize(); i++) {
			rList.add(memList.getModel().getElementAt(i));
		}
		return rList;
	}
	
	/**
	 * 接收消息
	 * @param msg 收到的消息
	 */
	public void recvMsg(Message msg) {
		msgList.addMessage(msg);
		this.validate();
		msgJsp.getVerticalScrollBar().setValue(msgJsp.getVerticalScrollBar().getMaximum());
	}
	
	/**
	 * 接收提示
	 * @param hint 提示内容
	 */
	public void recvHint(String hint) {
		msgList.addHint(hint);
		this.validate();
		msgJsp.getVerticalScrollBar().setValue(msgJsp.getVerticalScrollBar().getMaximum());
	}
	
	/**
	 * 更新群员列表
	 * @param list 新的成员列表
	 */
	public void updateMemberList(ArrayList<String> list) {
		String[] mList = new String[list.size()];
		for(int i = 0; i < list.size(); i++) {
			mList[i] = list.get(i);
		}
		memList.setListData(mList);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "sendMsg":
			Message newMessage = new Message(user.getName(), LocalDateTime.now(), this.message.getText());
			client.send("3 " + Client.replaceString(this.message.getText()));
			msgList.addMessage(newMessage);
			this.message.setText("");
			this.validate();
			break;
		}
	}
}
