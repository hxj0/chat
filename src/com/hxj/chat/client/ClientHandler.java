package com.hxj.chat.client;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.hxj.chat.client.frame.Chat;
import com.hxj.chat.client.frame.DouDong;
import com.hxj.chat.client.frame.Find;
import com.hxj.chat.client.frame.GroupChat;
import com.hxj.chat.client.frame.Login;
import com.hxj.chat.client.frame.Main;
import com.hxj.chat.common.constant.Constants;
import com.hxj.chat.common.entity.Message;
import com.hxj.chat.common.entity.User;
import com.hxj.chat.common.enums.MsgType;
import com.hxj.chat.common.ulist.ImageListModel;
import javazoom.jl.player.Player;


public class ClientHandler extends Thread{
	public static Map<Integer, JFrame> friendsChatFrame = new HashMap<>(); 

	private Map<String, Object> map = null; 
	
	@Override
	public void run() {
		while(true) {
			try {
				map = (HashMap<String, Object>)Login.ois.readObject();
			} catch (ClassNotFoundException | IOException e) {

				JOptionPane.showMessageDialog(null, "程序错误，与服务器断开链接，确认一秒后退出程序！","提示", JOptionPane.WARNING_MESSAGE, 
						new ImageIcon(ClientHandler.class.getResource("/com/hxj/chat/common/statics/icon/错误.png")));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.exit(0); 
			}
			System.out.println(map); 
			MsgType type = (MsgType) map.get("msgType");
			switch (type) {
				case LOGIN:
					handleFriendIsOnline(true);
					break;
				case CHAT:
					handleChat();
					break;
				case FIND:
					handleFind();
					break;
				case ADDFRIEND:
					handleAddFriend();
					break;
				case ADDFRIEND_RESULT:
					handleUList();
					break;
				case SEND_FILE:
					handleFile();
					break;
				case DELETE_FRIEND:
					handleUList();
					break;
				case UN_LOGIN:
					handleFriendIsOnline(false);
					break;
				case DD:
					handleDoudong();
					break;
				case CHAT_RECORD:
					handleChatRecord();
					break;
				case GROUP_CHAT:
					handleGroupChat();
					break;
				case ALL_USER:
					handleAllUser();
					break;
				case GROUP_CHAT_RECORD:
					handleGroupChatRecord();
					break;
				default:
					break;
			}
		}
	}

	private void handleGroupChatRecord() {
		GroupChat groupChat = (GroupChat)(friendsChatFrame.get(Constants.GROUP_NUMBER));
		groupChat.msgPane.setText("");
		int size = groupChat.model.getSize();
		List<Message> messages = (List<Message>)map.get("messages");
		for(Message msg : messages) {
			for(int i = 0; i < size; ++i) {
				if(msg.getFromId().equals(groupChat.model.getElementAt(i).getId())) {
					appendMsg(msg, groupChat.model.getElementAt(i).getNickname(), 
							groupChat.model.getElementAt(i).getImageIcon(),  groupChat.msgPane);
					break;
				}
			}
		}
	}

	private void handleAllUser() {
		GroupChat groupChat = (GroupChat)(friendsChatFrame.get(Constants.GROUP_NUMBER));
		List<User> userList = (List<User>) map.get("userList");
		System.out.println("用户列表"); 
		System.out.println(userList);
		groupChat.model.clear();
		groupChat.model.addAll(userList);
		groupChat.onlineList.repaint();
	}

	private void handleGroupChat() {
		Message msg = (Message) map.get("message");
		User user = (User)map.get("user");
		appendMsg(msg, user.getNickname(), user.getImageIcon(), ((GroupChat)(ClientHandler.friendsChatFrame.get(msg.getFromId()))).msgPane);
	}

	private void handleChatRecord() {
		User toUser = (User) map.get("toUser");
		((Chat)(ClientHandler.friendsChatFrame.get(toUser.getId()))).msgPane.setText("");
		List<Message> msgRecord = (List<Message>) map.get("msgRecord");
		if(msgRecord.size() == 0)return;
		for(Message msg : msgRecord) {
			if(msg.getFromId().equals(toUser.getId())) {
				appendMsg(msg, toUser.getNickname(), toUser.getImageIcon(), ((Chat)(ClientHandler.friendsChatFrame.get(msg.getFromId()))).msgPane);
			}else {
				msg.setFromId(msg.getToId());
				appendMsg(msg, Login.user.getNickname(), Login.user.getImageIcon(), ((Chat)(ClientHandler.friendsChatFrame.get(msg.getFromId()))).msgPane); 
			}
		}
	}

	private void handleDoudong() {
		int fromId = (int)map.get("fromId");
//		((Chat)(ClientHandler.friendsChatFrame.get(fromId)))
		new DouDong((ClientHandler.friendsChatFrame.get(fromId))).start();
	}

	private void handleFriendIsOnline(boolean online) {
		User friend = (User)map.get("friend");
		for(int i = 0; i < Main.friendList.getModel().getSize(); ++i) {
			User user = Main.friendList.getModel().getElementAt(i);
			if(user.getId().equals(friend.getId())){
				user.setIsOnline(online); 
				if(Main.remind) {
					if(online) {
						play(getClass().getResource("/com/hxj/chat/common/statics/keke.mp3").toString().substring(6));
					}else {
						play(getClass().getResource("/com/hxj/chat/common/statics/dada.mp3").toString().substring(6));
					}
				}
				Main.friendList.repaint();
			}
		}
	}

	private void handleFile() {
		//接收文件的时候可以指定具体的目录
		JFileChooser jfc = new JFileChooser();
		
		//只能选择文件夹
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//打开文件选择窗体
		int state = jfc.showDialog(new JLabel(), "保存文件");
		
		if(state == JFileChooser.CANCEL_OPTION) {
			System.out.println("用户取消下载");
			//取消
			return;
		}
		//获取文件
		File directory = jfc.getSelectedFile();
		String filePath = directory.getAbsolutePath();
		File saveFile = new File(filePath , (String)map.get("fileName"));
		try {
			FileOutputStream fos = new FileOutputStream(saveFile);
			fos.write((byte[])map.get("fileData")); 
			fos.flush();
			fos.close();
			if(friendsChatFrame.containsKey(map.get("fromId"))) {
				JTextPane pane = ((Chat)(ClientHandler.friendsChatFrame.get(map.get("fromId")))).msgPane;
				pane.
				getDocument().insertString(((Chat)(ClientHandler.friendsChatFrame.get(map.get("fromId")))).msgPane.getDocument().getLength(), "\n保存文件到"+saveFile.getAbsolutePath()+"成功！\n", null);
				pane.selectAll();
			} 
		} catch (IOException | BadLocationException e) {
			e.printStackTrace();
		}
		System.out.println("用户下载完成");
	}

	private void handleUList() {
		List<User> friends = (List<User>)map.get("friends");  
		List<Message> msgList = (List<Message>)map.get("msgList");  
		for(Message msg: msgList) {
			for(User user : friends) {
				if(user.getId().equals(msg.getFromId())) {
					user.setMsgCount(user.getMsgCount()+1);
				}
			}
		}
		ImageListModel model = new ImageListModel();
		model.addAll(friends);
		Main.friendList.setModel(model);
		for(User user : friends) {
			ClientHandler.friendsChatFrame.put(user.getId(), new Chat(user)); 
		}
	}

	private void handleAddFriend() {
		User user = (User)map.get("want");
		int res = JOptionPane.showConfirmDialog(null, "是否同意"+user.getId()+"的好友申请", "好友申请", 
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(Main.class.getResource("/com/hxj/chat/common/statics/icon/注册.png")));
		map.put("msgType", MsgType.ADDFRIEND_RESULT);
		map.put("res", res);
		try {
			Login.oos.writeObject(map);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleFind() {
		List<User> users = (List<User>)map.get("users");  
		ImageListModel model = new ImageListModel();
		model.addAll(users);
		Find.list.setModel(model);
	}

	private void handleChat() {
		Message msg = (Message) map.get("message");
		for(int i = 0; i < Main.friendList.getModel().getSize(); ++i) {
			User user = Main.friendList.getModel().getElementAt(i);
			if(user.getId().equals(msg.getFromId())){
				if(ClientHandler.friendsChatFrame.get(user.getId()).isVisible())user.setMsgCount(0);
				else user.setMsgCount(user.getMsgCount()+1);

				if(Main.remind) {
					play(getClass().getResource("/com/hxj/chat/common/statics/didi.mp3").toString().substring(6));
					System.out.println("提醒");
				}
				Chat chat = (Chat)(ClientHandler.friendsChatFrame.get(msg.getFromId()));
				appendMsg(msg, chat.toUser.getNickname(), chat.toUser.getImageIcon(), chat.msgPane);
				Main.friendList.repaint();
			}
		}
	}
	
	public void play(String url) {
		try {
			Player player = null;
			File mp3 = new File(url);
			FileInputStream fileInputStream = new FileInputStream(mp3);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			player = new Player(bufferedInputStream);
			player.play();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void appendMsg(Message msg, String nickName, ImageIcon imageIcon, JTextPane msgPane) { 
		if(msg.getContent() == null) return;
		// 添加一个可以设置样式的类
		StyleContext sc = StyleContext.getDefaultStyleContext();
		// 为所添加的样式类添加字体
		AttributeSet asetLine = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.FontFamily, msg.getFontFamily());
		asetLine = sc.addAttribute(asetLine, StyleConstants.FontSize, 16);
		String str = nickName+"  "+Chat.dtf.format(msg.getTime())+"\n"; 
//		((Chat)(ClientHandler.friendsChatFrame.get(msg.getFromId())))
		Document document = msgPane.getDocument(); 
 
		imageIcon.setImage(imageIcon.getImage().getScaledInstance(18,18,Image.SCALE_DEFAULT));
		msgPane.setCaretPosition(document.getLength());
		msgPane.insertIcon(imageIcon);
		try {
			document.insertString(document.getLength(), str, asetLine);
			int j = -1;
			for(int i = 0; i < msg.getContent().length(); ++i) {
				if(msg.getContent().startsWith("memoji", i) && (j = msg.getContent().indexOf(".gif", i)) != -1) {
					String iconPath = msg.getContent().substring(i+7, j+4);
					ImageIcon icon = new ImageIcon(iconPath);
					msgPane.setCaretPosition(document.getLength());
					msgPane.insertIcon(icon); 
					i = j+3;
				}else { 
					document.insertString(document.getLength(), msg.getContent().charAt(i)+"", asetLine);
				}
			}
			document.insertString(document.getLength(), "\n", asetLine);
			msgPane.selectAll();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
