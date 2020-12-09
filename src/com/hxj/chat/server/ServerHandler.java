package com.hxj.chat.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import com.hxj.chat.common.constant.Constants;
import com.hxj.chat.common.entity.Message;
import com.hxj.chat.common.entity.User;
import com.hxj.chat.common.enums.MsgType;
import com.hxj.chat.common.ulist.ImageListModel;
import com.hxj.chat.server.dao.MessageDao;
import com.hxj.chat.server.dao.UserDao;
import com.hxj.chat.server.frame.ServerFrame;

public class ServerHandler extends Thread{
	private static Map<Integer, OnlineUser> onlineUsers = new HashMap<>(); 
	
	private Socket socket;
	private ServerFrame serverFrame;
	private static UserDao userDao = new UserDao();
	private static MessageDao messageDao = new MessageDao();
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Integer id;

	private Map<String, Object> msgMap;
	public ServerHandler(Socket socket , ServerFrame serverFrame) {
		this.socket = socket;
		this.serverFrame = serverFrame;
	}

	@Override
	public void run() {
		//默认重复拿
		//模拟一直拿消息，产生阻塞
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			while(true) {
				msgMap = (HashMap<String, Object>)ois.readObject();
				System.out.println(msgMap);
				MsgType type = (MsgType) msgMap.get("msgType");
				switch (type) {
					case REGISTRY:
						handleRegistry();
						break;
					case LOGIN:
						handleLogin();
						break;
					case ULIST:
						handleUList(this.id);
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
						handleAddFriendResult();
						break;
					case SEND_FILE:
						handleFile();
						break;
					case DELETE_FRIEND:
						handleDeleteUser();
						break;
					case DD:
						handleDouDong();
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
				msgMap = null;
				
			}
		} catch (Exception e) {
			handleUnLogin(this.id);
			return;
		}		
	}
	
	
	private void handleGroupChatRecord() throws IOException { 
		List<Message> messages = messageDao.selectGroupMessage();
		msgMap.put("messages", messages);
		oos.writeObject(msgMap);
	}

	private void handleAllUser() throws IOException { 
		List<User> userList = userDao.selectAll();
		for(User user : userList) {
			user.setImageIcon(new ImageIcon(user.getIcon()));
		}
		msgMap.put("userList", userList);
		oos.writeObject(msgMap);
	}

	private void handleGroupChat() throws IOException {  
		handleChat();
	}

	private void handleChatRecord() throws IOException {
		User toUser = (User) msgMap.get("toUser");
		List<Message> msgRecord = messageDao.selectMsgRecord(this.id, toUser.getId());
		msgMap.put("msgRecord", msgRecord);
		onlineUsers.get(this.id).oos.writeObject(msgMap);
	}

	private void handleDouDong() throws IOException {
		int toId = (int)msgMap.get("toId");
		if(onlineUsers.containsKey(toId)) {
			onlineUsers.get(toId).oos.writeObject(msgMap);
		}else if(toId == Constants.GROUP_NUMBER) {
			msgMap.put("fromId", toId);
			for(Map.Entry<Integer, OnlineUser> entry : onlineUsers.entrySet()) {
				entry.getValue().oos.writeObject(msgMap);
			}
		}
			
	}

	public static void handleUnLogin(int id) {
		ImageListModel model = new ImageListModel(); 
		int count = 0;
		User user = onlineUsers.get(id).user; 
		try {
			onlineUsers.get(id).socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		onlineUsers.remove(id);
		for(Map.Entry<Integer, OnlineUser> entry : onlineUsers.entrySet()) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("msgType", MsgType.UN_LOGIN);
			map.put("friend", user);
			try {
				entry.getValue().oos.writeObject(map);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			model.addElement(entry.getValue().user);
			count++;
		}
		ServerFrame.numberLabel.setText(count+"人");
		ServerFrame.list.setModel(model);
	}

	private void handleDeleteUser() {
		userDao.deleteFriend(this.id, (Integer)msgMap.get("toId"));
		try {
			handleUList((Integer)msgMap.get("toId"));
			handleUList(this.id);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleFile() throws IOException {
		User user = (User)msgMap.get("toUser");
		msgMap.put("fromId", this.id);
		if(onlineUsers.containsKey(user.getId())) onlineUsers.get(user.getId()).oos.writeObject(msgMap);
	}

	private void handleAddFriendResult() throws IOException {
		int res = (int) msgMap.get("res");
		Message msg1 = (Message) msgMap.get("message");
		if(res == JOptionPane.YES_OPTION) {
			userDao.addFriend(msg1.getFromId(), msg1.getToId());
			handleUList(this.id);
			if(onlineUsers.containsKey(msg1.getFromId()))handleUList(msg1.getFromId());
		}else {
			userDao.confirmMsg(msg1.getFromId(), msg1.getToId());
		}
	}

	private void handleAddFriend() throws IOException {
		Message msg = (Message) msgMap.get("message");
		msgMap.put("want", onlineUsers.get(this.id).user);
		if(onlineUsers.containsKey(msg.getToId())) {
			onlineUsers.get(msg.getToId()).oos.writeObject(msgMap);
		}
		messageDao.addMessage(msg); 
	}

	private void handleFind() throws IOException {
		String likeId = (String) msgMap.get("likeId");
		List<User> findUsers = userDao.selectByLikeId(likeId, this.id);
		for(User user : findUsers) {
			user.setImageIcon(new ImageIcon(user.getIcon()));
		}
		msgMap.put("users", findUsers);
		onlineUsers.get(this.id).oos.writeObject(msgMap);
	}

	private void handleChat() throws IOException {
		Message message = (Message)msgMap.get("message");
		Integer toId = message.getToId();
		System.out.println(toId);
		if(onlineUsers.containsKey(toId)) {
			message.setType((byte)1); 
			onlineUsers.get(toId).oos.writeObject(msgMap);
		}else if(toId.equals(Constants.GROUP_NUMBER)) {
			message.setType((byte)1); 
//			System.out.println("进入group");
			Integer fromId = message.getFromId();
			message.setFromId(toId);
			msgMap.put("message", message);
			for(Map.Entry<Integer, OnlineUser> entry : onlineUsers.entrySet()) {
				if(entry.getKey().equals(fromId))continue;
				entry.getValue().oos.writeObject(msgMap);
			}
			message.setFromId(fromId);
		}
		messageDao.addMessage(message);
	}

	private void handleUList(int id) throws IOException {
		if(onlineUsers.containsKey(id)) {
			List<User> friends = userDao.getFriends(id);
			List<Message> msgList = messageDao.selectUndoMsg(id);
			System.out.println(msgList);
			for(User user : friends) {
				if(onlineUsers.containsKey(user.getId())) {
					user.setIsOnline(true);
				}else user.setIsOnline(false);
				user.setMsgCount(0);
				user.setImageIcon(new ImageIcon(user.getIcon()));
			}
			msgMap.put("msgList", msgList);
			msgMap.put("friends", friends);
			onlineUsers.get(id).oos.writeObject(msgMap);
			for(Message msg : msgList) {
				if(msg.getType() == 10) {
					Map<String, Object> map = new HashMap<>();
					map.put("msgType", MsgType.ADDFRIEND);
					User user = new User();
					user.setId(msg.getFromId());
					map.put("want", user);
					map.put("message", msg);
					onlineUsers.get(id).oos.writeObject(map);
				}
			}
		}
	}

	private void handleRegistry() throws IOException {
		User user1 = (User)msgMap.get("user");
		Boolean flag = false;
		Integer id = null;
		while(!flag) {
			id = (int)(Math.random()*899999+100000);
			flag = userDao.selectById(id) == null;
		}
		if(!user1.getIcon().equals(Constants.DEFAULT_ICON)) {
			byte[] iconData = (byte[])msgMap.get("iconData");
			String name = (String)msgMap.get("iconName");
			name = (int)(Math.random()*1000)+name;
			File saveFile = new File(Constants.PRE_DIR, name);
			user1.setIcon(saveFile.getAbsolutePath());
			System.out.println(saveFile.getAbsolutePath());
			try {
				FileOutputStream fos = new FileOutputStream(saveFile);
				fos.write(iconData); 
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		user1.setId(id);
		flag = userDao.addUser(user1);
		msgMap.put("flag", flag);
		msgMap.put("id", id);
		oos.writeObject(msgMap);
		
		List<User> userList = userDao.selectAll();
		for(User user : userList) {
			user.setImageIcon(new ImageIcon(user.getIcon()));
		}
		for(Map.Entry<Integer, OnlineUser> entry : onlineUsers.entrySet()) {
			Map<String, Object> map = new HashMap<>();
			map.put("userList", userList);
			map.put("msgType", MsgType.ALL_USER);
			entry.getValue().oos.writeObject(map);
		}
	}
	
	private void handleLogin() throws IOException {
		ImageListModel model = new ImageListModel();
		int count = 0;
		
		this.id = (Integer)msgMap.get("id");
		User user = userDao.selectOne(this.id, (String)msgMap.get("pwd"));
		user.setImageIcon(new ImageIcon(user.getIcon()));
		msgMap.put("user", user);
		if(user != null && !onlineUsers.containsKey(this.id)) {
			this.id = user.getId();
			user.setPassword(null);
			msgMap.put("flag", true);
			oos.writeObject(msgMap);
			for(Map.Entry<Integer, OnlineUser> entry : onlineUsers.entrySet()) {
				count++;
				HashMap<String, Object> map = new HashMap<>();
				map.put("msgType", MsgType.LOGIN);
				map.put("friend", user);
				entry.getValue().oos.writeObject(map);
				model.addElement(entry.getValue().user);
			}
			onlineUsers.put(this.id, new OnlineUser(socket, ois, oos, user));
			model.addElement(user);
			count++;
		}else {
			msgMap.put("flag", false);
			oos.writeObject(msgMap);
			return;
		}
		ServerFrame.list.setModel(model);
		ServerFrame.numberLabel.setText(count+"人");
	} 
}

class OnlineUser{
	Socket socket;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	User user;
	public OnlineUser(Socket socket, ObjectInputStream ois, ObjectOutputStream oos, User user) {
		this.user = user;
		this.socket = socket;
		this.ois = ois;
		this.oos = oos;
	}
}