package com.hxj.chat.client.frame;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Image;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import com.hxj.chat.common.constant.Constants;
import com.hxj.chat.common.entity.User;
import com.hxj.chat.common.enums.MsgType;

import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;

public class Register extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;
	private JTextField nickField;


	/**
	 * Create the frame.
	 */
	public Register() {

		setIconImage(Toolkit.getDefaultToolkit().getImage(Register.class.getResource("/com/hxj/chat/common/statics/注册.png")));
		setTitle("\u6CE8\u518C");
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 387, 295);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		JLabel iconLabel = new JLabel("头像:");
		iconLabel.setIcon(new ImageIcon(Register.class.getResource("/com/hxj/chat/common/statics/icon/头像.png")));
		iconLabel.setFont(new Font("����", Font.PLAIN, 14));
		iconLabel.setBounds(36, 48, 58, 15);
		getContentPane().add(iconLabel);
		
		JLabel pwdLabel = new JLabel("\u5BC6\u7801:");
		pwdLabel.setIcon(new ImageIcon(Register.class.getResource("/com/hxj/chat/common/statics/icon/密 码.png")));
		pwdLabel.setFont(new Font("����", Font.PLAIN, 14));
		pwdLabel.setBounds(36, 149, 58, 15);
		getContentPane().add(pwdLabel);
		
		JLabel confirmLabel = new JLabel("\u786E\u8BA4:");
		confirmLabel.setIcon(new ImageIcon(Register.class.getResource("/com/hxj/chat/common/statics/icon/确认.png")));
		confirmLabel.setFont(new Font("����", Font.PLAIN, 14));
		confirmLabel.setBounds(36, 191, 58, 15);
		getContentPane().add(confirmLabel);
		
		passwordField = new JPasswordField();
		passwordField.setToolTipText("密码");
		passwordField.setBounds(98, 147, 152, 22);
		getContentPane().add(passwordField);
		
		confirmPasswordField = new JPasswordField();
		confirmPasswordField.setToolTipText("确认密码");
		confirmPasswordField.setBounds(98, 189, 152, 22);
		getContentPane().add(confirmPasswordField);
		
		ImageIcon image =  new ImageIcon(Constants.DEFAULT_ICON);
		image.setImage(image.getImage().getScaledInstance(77,72,Image.SCALE_DEFAULT));
		JLabel icon = new JLabel(image);
		icon.setBounds(113, 24, 77, 72);
		getContentPane().add(icon);
		
		JButton chooseIconLabel = new JButton("选择头像");
		chooseIconLabel.setToolTipText("选择头像");
		chooseIconLabel.setIcon(new ImageIcon(Register.class.getResource("/com/hxj/chat/common/statics/icon/选择.png")));
		chooseIconLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = 0;
				String path = null;
				JFileChooser fileChooser = new JFileChooser();
				FileSystemView fsv = FileSystemView.getFileSystemView();  //注意了，这里重要的一句
				System.out.println(fsv.getHomeDirectory());                //得到桌面路径
				fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
				fileChooser.setDialogTitle("请选择要使用的头像");
				fileChooser.setApproveButtonText("确定");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG and jpg images", "png","jpg");
				fileChooser.setFileFilter(filter);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				result = fileChooser.showOpenDialog(null);
				if (JFileChooser.APPROVE_OPTION == result) {
				     path=fileChooser.getSelectedFile().getPath();
				     System.out.println("path: "+path);
				     ImageIcon image =  new ImageIcon(path);
				     image.setImage(image.getImage().getScaledInstance(77,72,Image.SCALE_DEFAULT));
				     icon.setIcon(image);
				     repaint();
				}
			}
		});
		chooseIconLabel.setBounds(226, 45, 116, 22);
		getContentPane().add(chooseIconLabel);
		

		
		JComboBox<String> sexComboBox = new JComboBox<>();
		sexComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"\u7537", "\u5973"}));
		sexComboBox.setBounds(98, 225, 58, 23);
		getContentPane().add(sexComboBox);
		
		JButton registBtn = new JButton("注册");
		registBtn.setToolTipText("确定注册");
		registBtn.setIcon(new ImageIcon(Register.class.getResource("/com/hxj/chat/common/statics/icon/注册.png")));
		registBtn.addActionListener(e -> {
			String nickname = nickField.getText().trim();
			@SuppressWarnings("deprecation")
			String pwd = passwordField.getText().trim();
			@SuppressWarnings("deprecation")
			String confirmPwd = confirmPasswordField.getText().trim();
			String msg = "";
			if(!pwd.equals(confirmPwd)) {
				msg = "两次输入密码不一致！";
			}
			if(nickname.equals("") || pwd.equals("") || confirmPwd.equals("")) {
				msg = "昵称或密码不能为空！";
			}
			if(!"".equals(msg)) {
				JOptionPane.showMessageDialog(null, msg,"提示", JOptionPane.WARNING_MESSAGE, 
						new ImageIcon(Register.class.getResource("/com/hxj/chat/common/statics/icon/提醒.png")));
				return;
			}

			User user = new User(null, nickname, pwd, icon.getIcon().toString(), sexComboBox.getSelectedIndex() == 0);
			try {
				Socket socket = new Socket(Constants.HOST_IP, Constants.SERVER_PORT);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				HashMap<String,Object> map = new HashMap<>();
				map.put("msgType", MsgType.REGISTRY);
				map.put("user", user);
				
				//获取文件
				File file = new File(user.getIcon()); 
				//读取文件内容字节数
				int fileLen;
				try {
					FileInputStream fis = new FileInputStream(file);
					fileLen = fis.available();
					byte [] fileData = new byte[fileLen];
					fis.read(fileData);//此时fileData中的数据就是文件数据
					fis.close();
					map.put("iconData", fileData);
					map.put("iconName", file.getName());
					System.out.println(map);
					oos.writeObject(map);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				map = (HashMap<String, Object>)ois.readObject(); 
				Boolean flag = (Boolean)map.get("flag");
				if(flag) {
					 Integer id = (Integer)map.get("id");
					 JOptionPane.showMessageDialog(null, "注册成功！账号为"+id+"\n确认后关闭注册界面","成功", JOptionPane.PLAIN_MESSAGE,
							 new ImageIcon(Register.class.getResource("/com/hxj/chat/common/statics/icon/成功.png")));
					 Thread.sleep(100); 
					 this.dispose();
					 
				}else {
					JOptionPane.showMessageDialog(null, "注册失败！","提示", JOptionPane.WARNING_MESSAGE, 
							new ImageIcon(Register.class.getResource("/com/hxj/chat/common/statics/icon/提醒.png")));
				}
				socket.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
		registBtn.setBounds(203, 225, 97, 23);
		getContentPane().add(registBtn);
		
		JLabel nickLabel = new JLabel("\u6635\u79F0:");
		nickLabel.setIcon(new ImageIcon(Register.class.getResource("/com/hxj/chat/common/statics/icon/昵称.png")));
		nickLabel.setFont(new Font("����", Font.PLAIN, 14));
		nickLabel.setBounds(36, 108, 58, 15);
		getContentPane().add(nickLabel);
		
		nickField = new JTextField();
		nickField.setToolTipText("昵称");
		nickField.setColumns(10);
		nickField.setBounds(98, 106, 152, 22);
		getContentPane().add(nickField);
		
		JLabel sexLabel = new JLabel("\u6027\u522B:");
		sexLabel.setIcon(new ImageIcon(Register.class.getResource("/com/hxj/chat/common/statics/icon/性别.png")));
		sexLabel.setFont(new Font("����", Font.PLAIN, 14));
		sexLabel.setBounds(36, 228, 58, 15);
		getContentPane().add(sexLabel);
		setVisible(true);
		setResizable(false);
	}
}
