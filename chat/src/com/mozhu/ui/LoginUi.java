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
 * ��¼����
 * @author Administrator
 *
 */
public class LoginUi extends JFrame{
	private static final long serialVersionUID = -2002438358389352125L;//��֪�����Ǹ�ɶ
	private JPanel up, down, user, pass;
	private JTextField usernameText;
	private JPasswordField passwordText;
	private JLabel u, p;
	private JButton login, registe;
	
	private Client client;
	
	public LoginUi(String host, int port) {
		initComponent();
		
		//��������
		this.setSize(500, 200);
		this.setTitle("��¼");
		this.setLayout(new FlowLayout());
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				client.send("0");
				System.exit(0);
			}
		});
		
		//���ӷ�����
		client = new Client();
		if(!client.Connection(host, port)) {
			JOptionPane.showMessageDialog(null, "����������ʧ��\n������������", "����ʧ��", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		//�����ͻ����������
		client.start();
	}
	
	/**
	 * ��ʼ�����
	 */
	private void initComponent() {
		//���
		up = new JPanel();
		down = new JPanel();
		user = new JPanel();
		pass = new JPanel();
		//�����
		usernameText = new JTextField(10);
		passwordText = new JPasswordField(10);
		//��ǩ
		u = new JLabel("��  ��");
		p = new JLabel("��  ��");
		//��ť
		login = new JButton("��¼");
		registe = new JButton("ע��");
		
		//�������
		login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//��¼����
				client.send("1 " + usernameText.getText() + " " + String.valueOf(passwordText.getPassword()));
				if(!client.waitReturn()) {
					JOptionPane.showMessageDialog(null, "��¼��ʱ", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				//��ȡ���
				ArrayList<String> result = client.getResult();
				if(result != null) {
					//�����û�
					User user = new User(Integer.valueOf(result.get(0)), result.get(1), result.get(2));
					//��ת��������
					new MainScreen(user, client);
					LoginUi.this.dispose();
				}else {
					JOptionPane.showMessageDialog(null, "�˺Ż��������", "����", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		registe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//ת��ע�����
				new RegisteUi(LoginUi.this, client);
			}
		});
		
		//�����
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
