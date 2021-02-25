package com.mozhu.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import com.mozhu.entity.Message;

/**
 * ��Ϣ�б�
 * @author Administrator
 *
 */
public class MessageList extends JPanel implements Scrollable{
	private static final long serialVersionUID = 1L;
	private ArrayList<JPanel> list;
	private Box box;
	
	public MessageList() {
		list = new ArrayList<JPanel>();
		box = Box.createVerticalBox();
		
		this.add(box);
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setSize(new Dimension(400,100));
		this.setMaximumSize(new Dimension(400,2000));
	}
	
	/**
	 * ����Ϣ�б����һ����Ϣ
	 * @param message Ҫ��ӵ���Ϣ
	 */
	public void addMessage(Message message) {
		MessageUi temp = new MessageUi(message);
		list.add(temp);
		box.add(temp);
	}
	
	/**
	 * ����Ϣ�б�ɾ��һ����Ϣ
	 * @param message Ҫɾ������Ϣ
	 */
	public void deleteMessage(Message message) {
		MessageUi temp = new MessageUi(message);
		list.remove(temp);
		box.remove(temp);
	}
	
	/**
	 * ����Ϣ�б���Ӷ�����Ϣ
	 * @param list ��Ϣ�б�
	 */
	public void addArrayMessage(ArrayList<Message> msgList) {
		for(int i = 0;i < msgList.size();i++) {
			list.add(new MessageUi(msgList.get(i)));
			box.add(list.get(i));
		}
	}
	
	/**
	 * ���һ����ʾ
	 * @param hint ��ʾ����
	 */
	public void addHint(String hint) {
		box.add(new HintUi(hint));
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(200,150);
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 20;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 20;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
}
