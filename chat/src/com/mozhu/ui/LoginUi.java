package com.mozhu.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.mozhu.entity.User;
import com.mozhu.net.Client;

/**
 * 登录界面
 * @author Administrator
 *
 */
public class LoginUi extends JFrame{
	private static final long serialVersionUID = -2002438358389352125L;//不知道这是个啥
	private JPanel up, down, user, pass;
	private JTextField usernameText;
	private JPasswordField passwordText;
	private JLabel u, p;
	private JButton login, registe;
	
	private Client client;
	
	public LoginUi(String host, int port) {
		initComponent();
		
		//窗口设置
		this.setSize(500, 200);
		this.setTitle("登录");
		this.setLayout(new FlowLayout());
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				client.send("0");
				System.exit(0);
			}
		});
		
		//连接服务器
		client = new Client();
		if(!client.Connection(host, port)) {
			JOptionPane.showMessageDialog(null, "服务器连接失败\n请检查您的网络", "连接失败", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		//启动客户端网络接收
		client.start();
	}
	
	/**
	 * 初始化组件
	 */
	private void initComponent() {
		//面板
		up = new JPanel();
		down = new JPanel();
		user = new JPanel();
		pass = new JPanel();
		//输入框
		usernameText = new JTextField(10);
		passwordText = new JPasswordField(10);
		//标签
		u = new JLabel("账  号");
		p = new JLabel("密  码");
		//按钮
		login = new JButton("登录");
		registe = new JButton("注册");
		
		//设置组件
		login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//登录命令
				client.send("1 " + usernameText.getText() + " " + String.valueOf(passwordText.getPassword()));
				if(!client.waitReturn()) {
					JOptionPane.showMessageDialog(null, "登录超时", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				//获取结果
				ArrayList<String> result = client.getResult();
				if(result != null) {
					//建立用户
					User user = new User(Integer.valueOf(result.get(0)), result.get(1), result.get(2));
					//跳转到主界面
					new MainScreen(user, client);
					LoginUi.this.dispose();
				}else {
					JOptionPane.showMessageDialog(null, "账号或密码错误", "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		registe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//转到注册界面
				new RegisteUi(LoginUi.this, client);
			}
		});
		
		//绑定组件
		user.add(u);
		user.add(usernameText);
		pass.add(p);
		pass.add(passwordText);
		up.add(user);
		up.add(pass);
		down.add(login);
		down.add(registe);
		this.add(up);
		this.add(down);
	}
}
