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
 * ע��Ի���
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
		super(frame, "ע��");
		this.client = client;
		this.isClash = false;
		init();
		this.setSize(300,250);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
	
	/**
	 * ��ʼ��
	 */
	private void init() {
		//�������
		jp = new JPanel();
		nnP = new JPanel();
		pwP = new JPanel();
		rpwP = new JPanel();
		bP = new JPanel();
		
		nickname = new JTextField(15);
		password = new JTextField(15);
		rePassword = new JTextField(15);
		hint = new JTextField(14);
		
		nn = new JLabel("�û�����");
		pw = new JLabel("���룺");
		rpw = new JLabel("ȷ�����룺");
		
		registe = new JButton("ע��");
		cancel = new JButton("ȡ��");
		//�������
		registe.addActionListener(this);
		cancel.addActionListener(this);
		nickname.addFocusListener(this);
		hint.setEditable(false);
		//�����
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
			//����û����Ƿ��ͻ
			if(isClash) {
				JOptionPane.showMessageDialog(this, "�û����ѱ�ʹ��", "���������û���", JOptionPane.WARNING_MESSAGE);
				return;
			}
			//���������ȷ�������Ƿ�һ��
			String nName = password.getText(), pWord = rePassword.getText();
			if(!nName.equals(pWord)) {
				JOptionPane.showMessageDialog(this, "���ٴμ��ȷ�������Ƿ�������һ��", "���ٴ�ȷ������", JOptionPane.WARNING_MESSAGE);
				return;
			}
			client.send("8 " + nickname.getText() + " " + pWord);
			if(!client.waitReturn()) {
				JOptionPane.showMessageDialog(this, "������δ��Ӧ", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			ArrayList<String> result = client.getResult();
			if(result != null) {
				JOptionPane.showMessageDialog(this, "ע��ɹ�\n�����˺ţ�" + result.get(0));
				this.dispose();
			} else {
				JOptionPane.showMessageDialog(this,"����ʧ�ܣ����Ժ�����", "�˺Ŵ���ʧ��", JOptionPane.ERROR_MESSAGE);
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
			//����û����Ƿ��ͻ
			client.send("9 " + nickname.getText() + " 0 0");
			if(!client.waitReturn()) {
				JOptionPane.showMessageDialog(this, "������δ��Ӧ", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			ArrayList<String> result = client.getResult();
			if(result != null) {
				this.isClash = true;
				hint.setText("�û����ѱ�ʹ��");
			} else {
				this.isClash = false;
				hint.setText("");
			}
		}
	}
}
