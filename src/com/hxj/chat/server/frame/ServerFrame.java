package com.hxj.chat.server.frame;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;

import com.hxj.chat.client.ClientHandler;
import com.hxj.chat.client.frame.Login;
import com.hxj.chat.client.frame.Main;
import com.hxj.chat.common.entity.User;
import com.hxj.chat.common.enums.MsgType;
import com.hxj.chat.common.ulist.ImageCellRenderer;
import com.hxj.chat.common.ulist.ImageListModel;
import com.hxj.chat.server.ServerHandler;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

/**
 * 服务器端主界面
 */
public class ServerFrame extends JFrame {
	public static JList<User> list;
	public static JLabel ipAndPort;
	public static JLabel numberLabel; 
	public static JLabel nameLabel;
	private static final long serialVersionUID = -8152378291726535742L;
	
	public ServerFrame() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(ServerFrame.class.getResource("/com/hxj/chat/common/statics/icon/服务器.png")));

		this.setTitle("网络聊天室服务器");
		//窗体不可扩大
		setResizable(false);
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(360, 503);
		setLocationRelativeTo(null);
		
		JLabel ipLabel = new JLabel("服务器ip地址:端口");
		ipLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		ipLabel.setBounds(23, 50, 132, 28);
		getContentPane().add(ipLabel);
		
		ipAndPort = new JLabel("192.168.0.108:8080");
		ipAndPort.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		ipAndPort.setBounds(150, 50, 149, 28);
		getContentPane().add(ipAndPort);
		
		JLabel uListLabel = new JLabel("[在线用户列表]");
		uListLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		uListLabel.setBounds(23, 88, 132, 28);
		getContentPane().add(uListLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(23, 116, 305, 334);
		getContentPane().add(scrollPane);
		
		list = new JList<User>();
		scrollPane.setViewportView(list);
		list.setCellRenderer(new ImageCellRenderer());
		list.setModel(new ImageListModel());
		
		//声明菜单
		JPopupMenu popupMenu = new JPopupMenu();
		
		//删除用户按钮（菜单项）
		JMenuItem privateChat = new JMenuItem("断开与此用户连接");
		privateChat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ServerHandler.handleUnLogin(list.getSelectedValue().getId());
			}
		}); 
		popupMenu.add(privateChat);
		
		
		//查看资料按钮（菜单项）
		JMenuItem blackList = new JMenuItem("查看资料");
		blackList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("查看资料");
			}
		});
		popupMenu.add(blackList);
		
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				//监听是左键还是右键
				if(e.isMetaDown()) {
					if(list.getSelectedIndex() >= 0) {
						//弹出菜单,JavaScript JS
						popupMenu.show(list , e.getX() , e.getY());
					}
				}
			}
		});
		JLabel ipLabel_1 = new JLabel("在线人数：");
		ipLabel_1.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		ipLabel_1.setBounds(127, 88, 72, 28);
		getContentPane().add(ipLabel_1);
		
		numberLabel = new JLabel("0人");
		numberLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		numberLabel.setBounds(195, 88, 72, 28);
		getContentPane().add(numberLabel);
		
		JLabel ipLabel_2 = new JLabel("服务器名称：");
		ipLabel_2.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		ipLabel_2.setBounds(23, 12, 132, 28);
		getContentPane().add(ipLabel_2);
		
		nameLabel = new JLabel("服务器名称");
		nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		nameLabel.setBounds(115, 12, 184, 28);
		getContentPane().add(nameLabel);
		setVisible(true);
	}
}
