package com.hxj.chat.client.frame;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.hxj.chat.common.constant.Constants;
import com.hxj.chat.common.entity.User;
import com.hxj.chat.common.enums.MsgType;
import com.hxj.chat.common.ulist.ImageCellRenderer;
import com.hxj.chat.common.ulist.ImageListModel;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;

import javax.swing.JComboBox;
import javax.swing.JList;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GroupChat extends JFrame {
	private JPanel contentPane;
	private FaceFrame faceFrame;
	public JTextPane msgPane;
	public JTextPane sendPane;
	public JList<User> onlineList;
	public ImageListModel model;

	/**
	 * Create the frame.
	 */
	public GroupChat() {
		setTitle(Login.user.getNickname()+"--群聊");
		setIconImage(Toolkit.getDefaultToolkit().getImage(GroupChat.class.getResource("/com/hxj/chat/common/statics/icon/群组.png")));

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setBounds(100, 100, 734, 561);
		setLocationRelativeTo(null); 
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 532, 301);
		contentPane.add(scrollPane);
		
		msgPane = new JTextPane();
		msgPane.setEditable(false);
		scrollPane.setViewportView(msgPane);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 345, 532, 138);
		contentPane.add(scrollPane_1);
		
		sendPane = new JTextPane();
		scrollPane_1.setViewportView(sendPane);
		
		JLabel faceLabel = new JLabel("");
		faceLabel.setIcon(new ImageIcon(GroupChat.class.getResource("/com/hxj/chat/common/statics/face.png")));
		faceLabel.setToolTipText("选择表情");
		faceLabel.setBounds(35, 321, 22, 21);
		contentPane.add(faceLabel);
		faceLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (faceFrame == null) {
					faceFrame = new FaceFrame();
		        } else faceFrame.setVisible(true); 
				faceFrame.textPane = sendPane;
		        Point point = MouseInfo.getPointerInfo().getLocation();
		        point.setLocation(point.getX(), point.getY() - 300);
		        faceFrame.setLocation(point);
			}
		});
		
		JButton sendBtn = new JButton("发送");
		sendBtn.setIcon(new ImageIcon(GroupChat.class.getResource("/com/hxj/chat/common/statics/icon/发送.png")));
		sendBtn.setFont(new Font("华文楷体", Font.PLAIN, 18));
		sendBtn.setBounds(441, 493, 101, 27);
		contentPane.add(sendBtn);
		sendBtn.addActionListener(e->{
			Chat.sendMsg(msgPane, sendPane, Constants.GROUP_NUMBER, MsgType.GROUP_CHAT);
		});
		
		JLabel douLabel = new JLabel("");
		douLabel.setIcon(new ImageIcon(GroupChat.class.getResource("/com/hxj/chat/common/statics/doudong.png")));
		douLabel.setToolTipText("发送抖动窗口");
		douLabel.setBounds(92, 321, 22, 21);
		contentPane.add(douLabel);
		douLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("toId", Constants.GROUP_NUMBER);
				map.put("fromId", Login.user.getId());
				map.put("msgType", MsgType.DD);
				try {
					Login.oos.writeObject(map);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
//				new DouDong(((GroupChat)(ClientHandler.friendsChatFrame.get(Constants.GROUP_NUMBER)))).start();
			}
		});
		
		JLabel lblNewLabel_1_1 = new JLabel("");
		lblNewLabel_1_1.setIcon(new ImageIcon(GroupChat.class.getResource("/com/hxj/chat/common/statics/ziti.png")));
		lblNewLabel_1_1.setBounds(153, 321, 22, 21);
		contentPane.add(lblNewLabel_1_1);
		
		JComboBox<String> fontFamilyCmb = new JComboBox<>();
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] str = graphicsEnvironment.getAvailableFontFamilyNames();
		for (String string : str) {
			fontFamilyCmb.addItem(string);
		}
		fontFamilyCmb.setSelectedItem("宋体");
		fontFamilyCmb.setToolTipText("选择字体"); 
		sendPane.setFont(new Font("宋体", Font.PLAIN, 16));
		fontFamilyCmb.addItemListener(e -> {
			System.out.println(e.getItem());
			sendPane.setFont(new Font((String)e.getItem(), Font.PLAIN, 16));
		});
		fontFamilyCmb.setBounds(201, 321, 118, 21);
		contentPane.add(fontFamilyCmb);
		
		JLabel chatRecord = new JLabel("");
		chatRecord.setIcon(new ImageIcon(GroupChat.class.getResource("/com/hxj/chat/common/statics/icon/聊天记录.png")));
		chatRecord.setToolTipText("查看聊天记录");
		chatRecord.setBounds(477, 321, 22, 21);
		chatRecord.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("msgType", MsgType.GROUP_CHAT_RECORD);
				try {
					Login.oos.writeObject(map);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		contentPane.add(chatRecord);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(552, 10, 168, 503);
		contentPane.add(scrollPane_2);
		
		onlineList = new JList<>();
		onlineList.setCellRenderer(new ImageCellRenderer());
		model = new ImageListModel();  
		onlineList.setModel(model); 
		scrollPane_2.setViewportView(onlineList);
		setResizable(false);
		setVisible(false);
		Map<String, Object> map = new HashMap<>();
		map.put("msgType", MsgType.ALL_USER);
		try {
			Login.oos.writeObject(map);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
}
