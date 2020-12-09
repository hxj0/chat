package com.hxj.chat.client.frame;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.hxj.chat.client.ClientHandler;
import com.hxj.chat.common.entity.Message;
import com.hxj.chat.common.entity.User;
import com.hxj.chat.common.enums.MsgType;

public class Chat extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private static FaceFrame faceFrame;
	public User toUser;
	
	public JTextPane msgPane;
	public JTextPane sendPane;
	public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"); 
	/**
	 * Create the frame.
	 */
	public Chat(User toUser) {
		this.toUser = toUser;
		setResizable(false); 
 
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setTitle("与"+toUser.getNickname()+"聊天");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Chat.class.getResource("/com/hxj/chat/common/statics/icon/聊天1.png")));
		setBounds(100, 100, 568, 564);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		setLocationRelativeTo(null);
		JLabel faceLabel = new JLabel("");
		faceLabel.setIcon(new ImageIcon(Chat.class.getResource("/com/hxj/chat/common/statics/face.png")));
		faceLabel.setBounds(20, 317, 22, 21);
		contentPane.add(faceLabel);
		JLabel douLabel = new JLabel("");
		douLabel.setIcon(new ImageIcon(Chat.class.getResource("/com/hxj/chat/common/statics/doudong.png")));
		douLabel.setBounds(64, 317, 22, 21);
		contentPane.add(douLabel);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 342, 532, 138);
		contentPane.add(scrollPane_1);
		sendPane = new JTextPane();

		scrollPane_1.setViewportView(sendPane);
		sendPane.requestFocus();
		sendPane.setFont(new Font("宋体", Font.PLAIN, 16));
		
		JComboBox<String> fontFamilyCmb = new JComboBox<>();
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] str = graphicsEnvironment.getAvailableFontFamilyNames();
		for (String string : str) {
			fontFamilyCmb.addItem(string);
		}
		fontFamilyCmb.setSelectedItem("宋体");
		fontFamilyCmb.setToolTipText("选择字体"); 
		fontFamilyCmb.addItemListener(e -> {
			System.out.println(e.getItem());
			sendPane.setFont(new Font((String)e.getItem(), Font.PLAIN, 16));
		});
		
		fontFamilyCmb.setBounds(140, 317, 118, 21);
		contentPane.add(fontFamilyCmb);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 532, 301);
		contentPane.add(scrollPane);
		
		msgPane = new JTextPane();
		msgPane.setEditable(false);
		scrollPane.setViewportView(msgPane);
		
		JButton sendBtn = new JButton("发送");
		sendBtn.setIcon(new ImageIcon(Chat.class.getResource("/com/hxj/chat/common/statics/icon/发送.png")));
		sendBtn.setFont(new Font("华文楷体", Font.PLAIN, 18)); 
		
		sendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMsg(msgPane, sendPane, toUser.getId(), MsgType.CHAT);
			}
		});
		sendBtn.setBounds(411, 490, 101, 27);
		contentPane.add(sendBtn);
		
		JButton fileBtn = new JButton("传文件");
		fileBtn.setIcon(new ImageIcon(Chat.class.getResource("/com/hxj/chat/common/statics/icon/文件 (1).png")));
		fileBtn.setFont(new Font("华文楷体", Font.PLAIN, 16));
		fileBtn.setBounds(20, 490, 110, 27);
		contentPane.add(fileBtn);
		fileBtn.addActionListener(e->{
			sendFile();
		});
		
		JLabel lblNewLabel_1_1 = new JLabel("");
		lblNewLabel_1_1.setIcon(new ImageIcon(Chat.class.getResource("/com/hxj/chat/common/statics/ziti.png")));
		lblNewLabel_1_1.setBounds(108, 317, 22, 21);
		contentPane.add(lblNewLabel_1_1);
		
		JLabel chatRecord = new JLabel("");
		chatRecord.setIcon(new ImageIcon(Chat.class.getResource("/com/hxj/chat/common/statics/icon/聊天记录.png")));
		chatRecord.setBounds(496, 317, 22, 21);
		contentPane.add(chatRecord); 
		chatRecord.setToolTipText("查看聊天记录");
		chatRecord.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Map<String, Object> map = new HashMap<>();
				map.put("msgType", MsgType.CHAT_RECORD);
				map.put("toUser", toUser);
				try {
					Login.oos.writeObject(map);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		setVisible(false);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});

		faceLabel.setToolTipText("选择表情");
		faceLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				getFaceFrame();
			}
		});

		douLabel.setToolTipText("发送抖动窗口");
		douLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("toId", toUser.getId());
				map.put("fromId", Login.user.getId());
				map.put("msgType", MsgType.DD);
				try {
					Login.oos.writeObject(map);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
//				((Chat)(ClientHandler.friendsChatFrame.get(toUser.getId())))
				new DouDong(((Chat)(ClientHandler.friendsChatFrame.get(toUser.getId())))).start();
			}
		});
	}
	


	public void getFaceFrame() {
		if (faceFrame == null) {
			faceFrame = new FaceFrame();
			System.out.println("创建");
        } else faceFrame.setVisible(true);
		faceFrame.textPane = sendPane;
        Point point = MouseInfo.getPointerInfo().getLocation();
        point.setLocation(point.getX(), point.getY() - 300);
        faceFrame.setLocation(point);
	}
	
	private void sendFile() {
		JFileChooser jfc = new JFileChooser();
		//设置选择文件是，可不可以选择文件夹
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		//打开文件选择窗体
		int state = jfc.showDialog(new JLabel(), "选择文件");
		
		if(state == JFileChooser.CANCEL_OPTION) {
			//取消
			return;
		}
		//获取文件
		File file = jfc.getSelectedFile();
		//读取文件内容字节数
		int fileLen;
		try {
			FileInputStream fis = new FileInputStream(file);
			fileLen = fis.available();
			byte [] fileData = new byte[fileLen];
			fis.read(fileData);//此时fileData中的数据就是文件数据
			fis.close();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("fileData", fileData);
			map.put("fileName", file.getName());
			map.put("toUser", toUser);
			map.put("msgType", MsgType.SEND_FILE);
			Login.oos.writeObject(map);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			msgPane.getDocument().insertString(msgPane.getDocument().getLength(), "\n发送文件"+file.getName()+"成功！\n", null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public static void sendMsg(JTextPane msgPane, JTextPane sendPane, Integer toId, MsgType msgType) {
		StyledDocument doc = sendPane.getStyledDocument();
		if(doc.getLength() == 0)return;
		Map<String, Object> map = new HashMap<>();

		// 添加一个可以设置样式的类
		StyleContext sc = StyleContext.getDefaultStyleContext();
//		 为所添加的样式类添加字体
		AttributeSet asetLine = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.FontFamily, sendPane.getFont().getFamily());
		asetLine = sc.addAttribute(asetLine, StyleConstants.FontSize, 16);
//		String text = sendPane.getText();
		StringBuilder sb = new StringBuilder();
		sendPane.requestFocus();
		LocalDateTime time = LocalDateTime.now();
		String timeStr = dtf.format(time);
		StyledDocument document = msgPane.getStyledDocument();
		ImageIcon imageIcon = Login.user.getImageIcon();
		imageIcon.setImage(imageIcon.getImage().getScaledInstance(18,18,Image.SCALE_DEFAULT));
		msgPane.setCaretPosition(document.getLength());
		msgPane.insertIcon(imageIcon);
		String str = " "+Login.user.getNickname()+" "+timeStr+"\n"; 
		try {
			document.insertString(document.getLength(), str,asetLine);
			System.out.println(doc.getLength());

			for(int i = 0; i < doc.getLength(); ++i) {
				if(doc.getCharacterElement(i).getName().equals("icon")) {
					Element element = doc.getCharacterElement(i);
					ImageIcon icon = (ImageIcon)StyleConstants.getIcon(element.getAttributes());
					msgPane.setCaretPosition(document.getLength());
					msgPane.insertIcon(icon);
//					System.out.println(icon);
					sb.append("memoji:"+icon);
				}else {
					 String s =doc.getText(i, 1);
					 document.insertString(document.getLength(), s,asetLine);
					 sb.append(s);
				}
			}

			document.insertString(document.getLength(), "\n",asetLine);
			map.put("msgType", msgType);
			System.out.println(sb.toString());
			Message message = new Message(Login.user.getId(), toId, time, (byte)0, sb.toString(), sendPane.getFont().getFamily());
			msgPane.selectAll();
			System.out.println(message);
			map.put("message", message);
			map.put("user", Login.user);
			Login.oos.writeObject(map);
			sendPane.setText("");
		} catch (IOException | BadLocationException e1) { 
			e1.printStackTrace();
		}
	}
}
