package com.mozhu.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.mozhu.entity.User;
import com.mozhu.net.Client;

/**
 * ������
 * @author Administrator
 *
 */
public class MainScreen extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JPanel bind, option, oBindL, oBindB, strange, sBind;
	private JTextField roomNameText;
	private JButton oConnect, oDelete, oCreate, oFlush, sConnect;
	private JLabel optionTip, roomNameL;
	private static JList<String> list;
	private JScrollPane jsp;
	
	private User user;
	private static Client client;
	
	public MainScreen(User user, Client client) {
		this.user = user;
		MainScreen.client = client;
		//��ʼ�����
		initComponent();
		//���ô���
		this.setBounds(200, 200, 700, 500);
		this.setTitle("�������");
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				client.send("0");
				//�ر�����ͻ���
				try {
					client.exit();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		this.setVisible(true);
		//����һ���̱߳���ÿ5��ˢ��һ���������б�
		new Thread(new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					MainScreen.client.send("6");
				}
			}
		}).start();
	}
	
	private void initComponent() {
		//���
		bind = new JPanel();
		option = new JPanel();
		oBindL = new JPanel();
		oBindB = new JPanel();
		strange = new JPanel();
		sBind = new JPanel();
		//�ı���
		roomNameText = new JTextField(15);
		//��ť
		oConnect = new JButton("����");
		oDelete = new JButton("ɾ��");
		oCreate = new JButton("�½�");
		oFlush = new JButton("ˢ��");
		sConnect = new JButton("ֱ������");
		//��ǩ
		optionTip = new JLabel("����������");
		roomNameL = new JLabel("����������");
		//�б��
		list = new JList<String>();
		jsp = new JScrollPane(list);
		
		//�������
		list.setVisibleRowCount(5);
		jsp.setSize(new Dimension(50,50));
		oConnect.setActionCommand("oConnect");
		oDelete.setActionCommand("oDelect");
		oCreate.setActionCommand("oCreate");
		oFlush.setActionCommand("oFlush");
		sConnect.setActionCommand("sConnect");
		oConnect.addActionListener(this);
		oDelete.addActionListener(this);
		oCreate.addActionListener(this);
		oFlush.addActionListener(this);
		sConnect.addActionListener(this);
		
		//�����
		oBindB.add(oConnect);
		oBindB.add(oDelete);
		oBindB.add(oCreate);
		oBindB.add(oFlush);
		oBindL.add(jsp);
		oBindL.add(oBindB);
		option.add(optionTip);
		option.add(oBindL);
		sBind.add(roomNameL);
		sBind.add(roomNameText);
		strange.add(sBind);
		strange.add(sConnect);
		bind.add(option);
		bind.add(strange);
		this.add(bind);
	}
	
	public static void updateChatRoomList(ArrayList<String> roomlist) {
		String[] newRoomList = new String[roomlist.size()];
		String[] oldRoomList = new String[list.getModel().getSize()];
		//��ȡ�б�
		for(int i = 0; i < roomlist.size(); i++) {
			newRoomList[i] = roomlist.get(i);
		}
		for(int i = 0; i < list.getModel().getSize(); i++) {
			oldRoomList[i] = list.getModel().getElementAt(i);
		}
		//�ж��¾��б��Ƿ�һ��
		if(newRoomList.length == oldRoomList.length) {
			int i;
			for(i = 0; i < roomlist.size(); i++) {
				if(!newRoomList[i].equals(oldRoomList[i])) {
					break;
				}
			}
			if(i != roomlist.size()) {
				list.setListData(newRoomList);
			}
		} else {
			list.setListData(newRoomList);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "oConnect"://����������
			String roomName = list.getSelectedValue();
			ArrayList<String> result = null;
			client.send("2 " + Client.replaceString(roomName));
			if(!client.waitReturn()) {
				JOptionPane.showMessageDialog(null, "������δ��Ӧ");
				return;
			}
			result = client.getResult();
			if(result != null) {
				//�ɹ�����ת�������ҽ���
				MultPeopleChatScreen screen = new MultPeopleChatScreen(roomName, user, client);
				client.setMultChatScreen(screen);
				client.send("7");
			}else {
				//����ʧ����ʾ
				JOptionPane.showMessageDialog(null, "����������ʧ��\n��ˢ���б��ٳ�������", "����ʧ��", JOptionPane.ERROR_MESSAGE);
			}
			break;
		case "oDelete":
			break;
		case "oCreate"://����������
			/*����Ӧ�ü��ϼ���������Ƿ�����*/
			String name = JOptionPane.showInputDialog(null, "����������������");
			ArrayList<String> rList = new ArrayList<String>();
			if(name == null) {
				return;
			}
			client.send("5 " + Client.replaceString(name));
			//��ȡJList�е�����ѡ��ֵ
			for(int i = 0; i < list.getModel().getSize(); i++) {
				rList.add(list.getModel().getElementAt(i));
			}
			rList.add(name);
			MainScreen.updateChatRoomList(rList);
			break;
		case "oFlush"://ˢ���������б�
			client.send("6");
			break;
		case "sConnect"://����������
			String roomName2 = roomNameText.getText().trim();
			ArrayList<String> result2 = null;
			client.send("2 " + Client.replaceString(roomName2));
			result2 = client.getResult();
			if(result2 != null) {
				//�ɹ�����ת�������ҽ���
				new MultPeopleChatScreen(roomName2, user, client);
			}else {
				//����ʧ����ʾ
				JOptionPane.showMessageDialog(null, "����������ʧ��\n���������������Ƿ�����", "����ʧ��", JOptionPane.ERROR_MESSAGE);
			}
			break;
		}
	}
}
