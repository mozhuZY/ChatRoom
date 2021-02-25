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
 * 主界面
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
		//初始化组件
		initComponent();
		//设置窗口
		this.setBounds(200, 200, 700, 500);
		this.setTitle("话痨软件");
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				client.send("0");
				//关闭网络客户端
				try {
					client.exit();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		this.setVisible(true);
		//创建一个线程保持每5秒刷新一次聊天室列表
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
		//面板
		bind = new JPanel();
		option = new JPanel();
		oBindL = new JPanel();
		oBindB = new JPanel();
		strange = new JPanel();
		sBind = new JPanel();
		//文本框
		roomNameText = new JTextField(15);
		//按钮
		oConnect = new JButton("连接");
		oDelete = new JButton("删除");
		oCreate = new JButton("新建");
		oFlush = new JButton("刷新");
		sConnect = new JButton("直接连接");
		//标签
		optionTip = new JLabel("连接聊天室");
		roomNameL = new JLabel("聊天室名称");
		//列表框
		list = new JList<String>();
		jsp = new JScrollPane(list);
		
		//组件设置
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
		
		//组件绑定
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
		//获取列表
		for(int i = 0; i < roomlist.size(); i++) {
			newRoomList[i] = roomlist.get(i);
		}
		for(int i = 0; i < list.getModel().getSize(); i++) {
			oldRoomList[i] = list.getModel().getElementAt(i);
		}
		//判断新旧列表是否一致
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
		case "oConnect"://连接聊天室
			String roomName = list.getSelectedValue();
			ArrayList<String> result = null;
			client.send("2 " + Client.replaceString(roomName));
			if(!client.waitReturn()) {
				JOptionPane.showMessageDialog(null, "服务器未响应");
				return;
			}
			result = client.getResult();
			if(result != null) {
				//成功则跳转到聊天室界面
				MultPeopleChatScreen screen = new MultPeopleChatScreen(roomName, user, client);
				client.setMultChatScreen(screen);
				client.send("7");
			}else {
				//连接失败提示
				JOptionPane.showMessageDialog(null, "聊天室连接失败\n请刷新列表再尝试连接", "连接失败", JOptionPane.ERROR_MESSAGE);
			}
			break;
		case "oDelete":
			break;
		case "oCreate"://创建聊天室
			/*后续应该加上检测聊天室是否重名*/
			String name = JOptionPane.showInputDialog(null, "请输入聊天室名称");
			ArrayList<String> rList = new ArrayList<String>();
			if(name == null) {
				return;
			}
			client.send("5 " + Client.replaceString(name));
			//获取JList中的所有选项值
			for(int i = 0; i < list.getModel().getSize(); i++) {
				rList.add(list.getModel().getElementAt(i));
			}
			rList.add(name);
			MainScreen.updateChatRoomList(rList);
			break;
		case "oFlush"://刷新聊天室列表
			client.send("6");
			break;
		case "sConnect"://进入聊天室
			String roomName2 = roomNameText.getText().trim();
			ArrayList<String> result2 = null;
			client.send("2 " + Client.replaceString(roomName2));
			result2 = client.getResult();
			if(result2 != null) {
				//成功则跳转到聊天室界面
				new MultPeopleChatScreen(roomName2, user, client);
			}else {
				//连接失败提示
				JOptionPane.showMessageDialog(null, "聊天室连接失败\n请检查聊天室名称是否有误", "连接失败", JOptionPane.ERROR_MESSAGE);
			}
			break;
		}
	}
}
