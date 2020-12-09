package com.hxj.chat.client.frame;

import java.awt.Graphics;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class BackGroundImagePanel extends JPanel{
	private ImageIcon icon;
	
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
	
	public BackGroundImagePanel(URL url) {
		icon = new ImageIcon(url);
	}
	public void paintComponent(Graphics g) {
		g.drawImage(icon.getImage(), 0, 0, getSize().width,getSize().height, this);
	}
}
