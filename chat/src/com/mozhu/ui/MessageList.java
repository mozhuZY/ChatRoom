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
 * 信息列表
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
	 * 向信息列表添加一条信息
	 * @param message 要添加的信息
	 */
	public void addMessage(Message message) {
		MessageUi temp = new MessageUi(message);
		list.add(temp);
		box.add(temp);
	}
	
	/**
	 * 从信息列表删除一条信息
	 * @param message 要删除的信息
	 */
	public void deleteMessage(Message message) {
		MessageUi temp = new MessageUi(message);
		list.remove(temp);
		box.remove(temp);
	}
	
	/**
	 * 向信息列表添加多条信息
	 * @param list 信息列表
	 */
	public void addArrayMessage(ArrayList<Message> msgList) {
		for(int i = 0;i < msgList.size();i++) {
			list.add(new MessageUi(msgList.get(i)));
			box.add(list.get(i));
		}
	}
	
	/**
	 * 添加一条提示
	 * @param hint 提示内容
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
