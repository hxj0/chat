package com.hxj.chat.client.frame;

import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

/**
 * 表情框
 */
public class FaceFrame extends JFrame{
	private static final long serialVersionUID = 8603409755227097846L;
	public JTextPane textPane;
	/**
	 * 表情图片摆放
	 * @param textPane
	 */
	public FaceFrame(){ 
		setIconImage(Toolkit.getDefaultToolkit().getImage(Chat.class.getResource("/com/hxj/chat/common/statics/icon/猴子.png")));
		JPanel panel=(JPanel)getContentPane();
		panel.setLayout(null);
		//用双重循环来摆图片
		JLabel lblIcon;
		ImageIcon icon;
		for (int row = 0; row <10; row++) {
			for (int col = 0; col < 6; col++) {
				//得到图片
				icon=new ImageIcon("src/com/hxj/chat/common/statics/face/"+(6*row+col+1)+".gif");
				//将图片放在JLable里
				lblIcon=new JLabel(icon);
				lblIcon.setSize(50,50);
				lblIcon.setLocation(col * 50, row * 50);
				//为lbl添加鼠标事件
				lblIcon.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						JLabel jLabel = (JLabel)e.getSource();
						Icon icon2 = jLabel.getIcon();
//						System.out.println(icon2);
						//插入选中图片 
						textPane.insertIcon(icon2);
						//关闭当前图片框
						setVisible(false);
					}
				});
				panel.add(lblIcon);
			}
		}

		setSize(320, 300);
		setTitle("嘻哈猴");
		setVisible(true);
		setResizable(false);
		this.addWindowStateListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
	}
}
