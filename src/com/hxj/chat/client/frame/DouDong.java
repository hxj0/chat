package com.hxj.chat.client.frame;

import javax.swing.JFrame;

/**
 * 抖动类
 */
public class DouDong extends Thread{

	JFrame frame;
	
	public DouDong(JFrame frame) {
		this.frame = frame;
	}
	
	@Override
	public void run() {
		try {
			for (int i = 0; i < 3; i++) {
				frame.setLocation(frame.getX()-28, frame.getY());
				Thread.sleep(88);
				frame.setLocation(frame.getX(), frame.getY()-28);
				Thread.sleep(88);
				frame.setLocation(frame.getX()+28, frame.getY());
				Thread.sleep(88);
				frame.setLocation(frame.getX(), frame.getY()+28);
				Thread.sleep(88);
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
}