package com.hxj.chat.client.frame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.hxj.chat.client.ClientHandler;
import com.hxj.chat.common.constant.Constants;
import com.hxj.chat.common.entity.Message;
import com.hxj.chat.common.entity.User;
import com.hxj.chat.common.enums.MsgType;
import com.hxj.chat.common.ulist.ImageListModel;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream; 
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import javax.swing.ImageIcon;
import java.awt.Toolkit;

public class Login extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField idField;
	private JPasswordField passwordField; 
	public static Socket socket;
	public static User user;
	public static ObjectOutputStream oos;
	public static ObjectInputStream ois;
//	public static HashMap<String, Object> map;
	
	/** 
	 * Launch the application.
	 */
	public static void main(String[] args) {
//		try {
//			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
//				| UnsupportedLookAndFeelException e1) {
//			e1.printStackTrace();
//		}
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					Login frame = new Login();
//					frame.setResizable(false);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
	}

	/**
	 * Create the frame.
	 */
	public Login() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Login.class.getResource("/com/hxj/chat/common/statics/chat.png")));
		setTitle("登录");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 336, 209);
		setLocationRelativeTo(null);
		contentPane = new BackGroundImagePanel(Constants.BACKGROUD_IMAGE);
		
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel idLabel = new JLabel("账号：");
		idLabel.setIcon(new ImageIcon(Login.class.getResource("/com/hxj/chat/common/statics/icon/账号.png")));
		idLabel.setBounds(51, 26, 89, 38);
		contentPane.add(idLabel);
		
		JLabel pwdLabel = new JLabel("密码：");
		pwdLabel.setIcon(new ImageIcon(Login.class.getResource("/com/hxj/chat/common/statics/icon/密 码.png")));
		pwdLabel.setBounds(51, 74, 89, 38);
		contentPane.add(pwdLabel);
		
		idField = new JTextField();
		idField.setBounds(105, 31, 177, 29);
		contentPane.add(idField);
		idField.setColumns(10);
		
		JButton registBtn = new JButton("注册");
		registBtn.setIcon(new ImageIcon(Login.class.getResource("/com/hxj/chat/common/statics/icon/注册.png")));
		registBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Register();
			}
		});
		registBtn.setBounds(51, 122, 97, 23);
		contentPane.add(registBtn);
		
		JButton loginBtn = new JButton("登录");
		loginBtn.setIcon(new ImageIcon(Login.class.getResource("/com/hxj/chat/common/statics/icon/登录.png")));
		loginBtn.addActionListener(e->{
			login();
		});
		loginBtn.setBounds(172, 122, 97, 23);
		contentPane.add(loginBtn);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(105, 74, 177, 29);
		contentPane.add(passwordField);
		setVisible(true);setResizable(false);
	}

	private void login() {
		String idStr = idField.getText().trim();
		@SuppressWarnings("deprecation")
		String pwd = passwordField.getText().trim();
		Integer id = null;
		String msg = "";
		if(idStr.length() > 11) {
			msg = "账号过长";
		}
		try {
			id = Integer.valueOf(idStr);
		} catch (Exception e2) {
			msg = "账号中只能含有数字！";
		}
		if(idStr.equals("") || pwd.equals("")) {
			msg = "账号或密码不能为空！";
		}
		if(!"".equals(msg)) {
			JOptionPane.showMessageDialog(null, msg,"提示", JOptionPane.WARNING_MESSAGE, 
					new ImageIcon(Register.class.getResource("/com/hxj/chat/common/statics/icon/提醒.png")));
			return;
		}
		try {
			Login.socket = new Socket(Constants.HOST_IP, Constants.SERVER_PORT);
			oos = new ObjectOutputStream(socket.getOutputStream());
			Map<String, Object> map = new HashMap<>();
			map.put("msgType", MsgType.LOGIN);
			map.put("id", id);
			map.put("pwd", pwd);
			oos.writeObject(map);
			
			ois = new ObjectInputStream(socket.getInputStream());
			map = (HashMap<String, Object>)ois.readObject();
			Boolean flag = (Boolean)map.get("flag");
			if(flag) {
				Login.user = (User)map.get("user");
				map.put("msgType", MsgType.ULIST);
				oos.writeObject(map);
//				System.out.println(1);
				map = (Map<String, Object>)ois.readObject();
				List<Message> msgList = (List<Message>)map.get("msgList");
				List<User> friends= (List<User>) map.get("friends");
				ImageListModel model = new ImageListModel();
				model.addAll(friends);
				new ClientHandler().start();
				for(User user : friends) {
					ClientHandler.friendsChatFrame.put(user.getId(), new Chat(user)); 
				}
				for(Message message : msgList) {
					if(!ClientHandler.friendsChatFrame.containsKey(message.getFromId())) {
						continue;
					}
					Chat chat = (Chat)(ClientHandler.friendsChatFrame.get(message.getFromId())); 
					ClientHandler.appendMsg(message, chat.toUser.getNickname(), chat.toUser.getImageIcon(), chat.msgPane);
					if(message.getFromId().equals(chat.toUser.getId())) chat.toUser.setMsgCount(chat.toUser.getMsgCount()+1);
				}
				ClientHandler.friendsChatFrame.put(Constants.GROUP_NUMBER, new GroupChat()); 
				new Main(Login.user, model);
				System.out.println(Login.user);
				setVisible(false);
				map.clear();
			}else {
				if(map.get("user") != null)msg = "用户已登录！";
				else msg = "用户名或密码错误！";
				JOptionPane.showMessageDialog(null, msg,"提示", JOptionPane.WARNING_MESSAGE, 
						new ImageIcon(Register.class.getResource("/com/hxj/chat/common/statics/icon/提醒.png")));
			}
		} catch (IOException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	}
}
