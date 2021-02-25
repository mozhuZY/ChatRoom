package com.mozhu.ui;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.mozhu.entity.Message;

/**
 * 信息面板
 * @author Administrator
 *
 */
public class MessageUi extends JPanel{
	private static final long serialVersionUID = 1L;
	private JPanel userMsg;
	private JTextArea msg;
	private Box box;
	
	private Message message;
	
	public MessageUi(Message message) {
		this.message = message;
		//初始化组件
		initComponent();
		//面板设置
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBackground(Color.white);
	}
	
	private void initComponent() {
		//初始化
		box = Box.createVerticalBox();
		userMsg = new JPanel();
		msg = new JTextArea(message.getMessage());
		
		//组件设置
		msg.setEditable(false);
		
		//绑定组件
		userMsg.setLayout(new FlowLayout(FlowLayout.LEFT));
		userMsg.setBackground(Color.white);
		userMsg.add(new JLabel(message.getUsername() + " " + message.getTime()));
		box.add(userMsg);
		box.add(msg);
		this.add(box);
	}
	
	public Message getMessage() {
		return message;
	}
	
	public void setMessage(Message message) {
		this.message = message;
	}
}
