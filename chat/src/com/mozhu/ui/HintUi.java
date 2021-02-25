package com.mozhu.ui;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class HintUi extends JPanel{
	private static final long serialVersionUID = 7683787652665658314L;
	private JLabel jb;
	
	public HintUi(String hintText) {
		super();
		jb = new JLabel(hintText);
		this.add(jb);
		this.setPreferredSize(new Dimension(310, 20));
	}
}
