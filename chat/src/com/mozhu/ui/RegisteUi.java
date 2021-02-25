package com.mozhu.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mozhu.net.Client;

/**
 * 注册对话框
 * @author Administrator
 *
 */
public class RegisteUi extends JDialog implements ActionListener, FocusListener{
	private static final long serialVersionUID = -5664373329320175783L;
	private JPanel jp, nnP, pwP, rpwP, bP; 
	private JTextField nickname, password, rePassword, hint;
	private JLabel nn, pw, rpw;
	private JButton registe, cancel;
	
	private Client client;
	private boolean isClash;
	
	public RegisteUi(JFrame frame, Client client) {
		super(frame, "注册");
		this.client = client;
		this.isClash = false;
		init();
		this.setSize(300,250);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
	
	/**
	 * 初始化
	 */
	private void init() {
		//创建组件
		jp = new JPanel();
		nnP = new JPanel();
		pwP = new JPanel();
		rpwP = new JPanel();
		bP = new JPanel();
		
		nickname = new JTextField(15);
		password = new JTextField(15);
		rePassword = new JTextField(15);
		hint = new JTextField(14);
		
		nn = new JLabel("用户名：");
		pw = new JLabel("密码：");
		rpw = new JLabel("确认密码：");
		
		registe = new JButton("注册");
		cancel = new JButton("取消");
		//设置组件
		registe.addActionListener(this);
		cancel.addActionListener(this);
		nickname.addFocusListener(this);
		hint.setEditable(false);
		//绑定组件
		nnP.add(nn);
		nnP.add(nickname);
		pwP.add(pw);
		pwP.add(password);
		rpwP.add(rpw);
		rpwP.add(rePassword);
		bP.add(registe);
		bP.add(cancel);
		jp.add(nnP);
		jp.add(hint);
		jp.add(pwP);
		jp.add(rpwP);
		jp.add(bP);
		this.add(jp);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(registe)) {
			//检查用户名是否冲突
			if(isClash) {
				JOptionPane.showMessageDialog(this, "用户名已被使用", "重新输入用户名", JOptionPane.WARNING_MESSAGE);
				return;
			}
			//检查密码与确认密码是否一致
			String nName = password.getText(), pWord = rePassword.getText();
			if(!nName.equals(pWord)) {
				JOptionPane.showMessageDialog(this, "请再次检查确认密码是否与密码一致", "请再次确认密码", JOptionPane.WARNING_MESSAGE);
				return;
			}
			client.send("8 " + nickname.getText() + " " + pWord);
			if(!client.waitReturn()) {
				JOptionPane.showMessageDialog(this, "服务器未响应", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			ArrayList<String> result = client.getResult();
			if(result != null) {
				JOptionPane.showMessageDialog(this, "注册成功\n您的账号：" + result.get(0));
				this.dispose();
			} else {
				JOptionPane.showMessageDialog(this,"创建失败，请稍后重试", "账号创建失败", JOptionPane.ERROR_MESSAGE);
			}
		} else if(e.getSource().equals(cancel)) {
			this.dispose();
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		if(e.getSource().equals(nickname)) {
			//检查用户名是否冲突
			client.send("9 " + nickname.getText() + " 0 0");
			if(!client.waitReturn()) {
				JOptionPane.showMessageDialog(this, "服务器未响应", "提示", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			ArrayList<String> result = client.getResult();
			if(result != null) {
				this.isClash = true;
				hint.setText("用户名已被使用");
			} else {
				this.isClash = false;
				hint.setText("");
			}
		}
	}
}
