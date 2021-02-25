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
 * ���������ҽ���
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
		//��ʼ�����
		initComponent();
		
		//��������
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
		//���
		bind = new JPanel(new GridLayout(2, 1, 0, 0));
		bindUp = new JPanel(new GridLayout(1, 2, 0, 0));
		sendMsgPanel = new JPanel(new BorderLayout());
		memberPanel = new JPanel(new BorderLayout());
		//��Ϣ�б�
		msgList = new MessageList();
		msgJsp = new JScrollPane(msgList);
		//�б��
		memList = new JList<String>();
		memJsp = new JScrollPane(memList);
		//�ı�����
		message = new JTextArea(6,20);
		sendMsgJsp = new JScrollPane(message);
		//��ǩ
		memberL = new JLabel("�����ҳ�Ա");
		//��ť
		sendMsg = new JButton("����");
		
		//�������
		msgList.setBounds(10, 10, this.getWidth() / 2 - 10, this.getHeight() / 2 - 10);
		msgList.setBackground(Color.white);
		memList.setVisibleRowCount(10);
		sendMsg.setActionCommand("sendMsg");
		sendMsg.addActionListener(this);
		
		//�����
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
	 * ��ȡ��Ϣ�б�
	 * @return ��Ϣ�б� MessageList
	 */
	public MessageList getMsgList() {
		return msgList;
	}
	
	/**
	 * ��ȡȺԱ�б�
	 * @return ȺԱ�б� ArrayList<String>
	 */
	public ArrayList<String> getMemberList() {
		ArrayList<String> rList = new ArrayList<String>();
		for(int i = 0; i < memList.getModel().getSize(); i++) {
			rList.add(memList.getModel().getElementAt(i));
		}
		return rList;
	}
	
	/**
	 * ������Ϣ
	 * @param msg �յ�����Ϣ
	 */
	public void recvMsg(Message msg) {
		msgList.addMessage(msg);
		this.validate();
		msgJsp.getVerticalScrollBar().setValue(msgJsp.getVerticalScrollBar().getMaximum());
	}
	
	/**
	 * ������ʾ
	 * @param hint ��ʾ����
	 */
	public void recvHint(String hint) {
		msgList.addHint(hint);
		this.validate();
		msgJsp.getVerticalScrollBar().setValue(msgJsp.getVerticalScrollBar().getMaximum());
	}
	
	/**
	 * ����ȺԱ�б�
	 * @param list �µĳ�Ա�б�
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
