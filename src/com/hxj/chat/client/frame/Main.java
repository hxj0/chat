package com.hxj.chat.client.frame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;

import com.hxj.chat.client.ClientHandler;
import com.hxj.chat.common.constant.Constants;
import com.hxj.chat.common.entity.User;
import com.hxj.chat.common.enums.MsgType;
import com.hxj.chat.common.ulist.FriendImageCellRenderer;
import com.hxj.chat.common.ulist.ImageCellRenderer;
import com.hxj.chat.common.ulist.ImageListModel;

import java.awt.AWTException;
import java.awt.Color;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Find findFrame;
	public static JList<User> friendList;

	public static Boolean remind = false;

	/**
	 * Create the frame.
	 */
	public Main(User user, ImageListModel model) {
		addTray();
		setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/com/hxj/chat/common/statics/icon/聊天.png")));
		setTitle(user.getNickname()+"的主页");
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 281, 559);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBounds(0, 471, 263, 57);
		contentPane.add(bottomPanel);
		bottomPanel.setLayout(null);
		
		JButton findBtn = new JButton("查找好友/群聊");
		findBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(findFrame == null)findFrame = new Find();
				findFrame.setVisible(true);
			}
		});
		findBtn.setIcon(new ImageIcon(Main.class.getResource("/com/hxj/chat/common/statics/icon/查找.png")));
		findBtn.setBounds(10, 10, 136, 37);
		bottomPanel.add(findBtn);
		
		JButton noticeBtn = new JButton("消息提醒");
		noticeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!Main.remind) {
					noticeBtn.setText("取消提醒");
				}else {
					noticeBtn.setText("消息提醒");
				}
				Main.remind = !Main.remind;
			}
		});
		noticeBtn.setIcon(new ImageIcon(Main.class.getResource("/com/hxj/chat/common/statics/icon/提醒.png")));
		noticeBtn.setBounds(156, 10, 107, 37);
		bottomPanel.add(noticeBtn);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 73, 263, 399);
		contentPane.add(tabbedPane);
		tabbedPane.setFont(new Font("宋体", Font.PLAIN, 14));
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("联系人", new ImageIcon(Main.class.getResource("/com/hxj/chat/common/statics/icon/联系人.png")), scrollPane, null);
		
		
		friendList = new JList<>();
		friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		friendList.setModel(model);
		friendList.setCellRenderer(new FriendImageCellRenderer());
		scrollPane.setViewportView(friendList);
		
		//声明菜单
		JPopupMenu popupMenu = new JPopupMenu();
		
		//删除好友按钮（菜单项）
		JMenuItem privateChat = new JMenuItem("删除好友");
		privateChat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.showConfirmDialog(null, "是否确认删除好友"+friendList.getSelectedValue().getNickname(),
						"确认", JOptionPane.YES_NO_OPTION,
						JOptionPane.ERROR_MESSAGE,new ImageIcon(Main.class.getResource("/com/hxj/chat/common/statics/icon/删除好友3.png")));
				if(confirm == JOptionPane.NO_OPTION)return;
				
				Map<String, Object> map = new HashMap<String, Object>(); 
				map.put("msgType", MsgType.DELETE_FRIEND);
				map.put("toId", friendList.getSelectedValue().getId());
				try {
					Login.oos.writeObject(map);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
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
	
						
		
		friendList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					User toUser = friendList.getSelectedValue();
					toUser.setMsgCount(0);
					System.out.println(toUser);
					ClientHandler.friendsChatFrame.get(toUser.getId()).setVisible(true);
				}
				//监听是左键还是右键
				if(e.isMetaDown()) {
					if(friendList.getSelectedIndex() >= 0) {
						//弹出菜单,JavaScript JS
						popupMenu.show(friendList , e.getX() , e.getY());
					}
				}
			}
		});
		
		
		
		JScrollPane scrollPane_1 = new JScrollPane();
		tabbedPane.addTab("群聊", new ImageIcon(Main.class.getResource("/com/hxj/chat/common/statics/icon/群聊.png")), scrollPane_1, null);
		
		JList<User> groupList = new JList<>(); 
		scrollPane_1.setViewportView(groupList);
		groupList.setCellRenderer(new ImageCellRenderer());
		ImageListModel groupmodel = new ImageListModel();
		User user2 = new User(Constants.GROUP_NUMBER, "群组", "", new ImageIcon(Main.class.getResource("/com/hxj/chat/common/statics/groupIcon.jpg")).toString().substring(6), false);
		user2.setImageIcon(new ImageIcon(Main.class.getResource("/com/hxj/chat/common/statics/groupIcon.jpg")));
		groupmodel.addElement(user2); 
		groupList.setModel(groupmodel);
		groupList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					ClientHandler.friendsChatFrame.get(Constants.GROUP_NUMBER).setVisible(true); 
				}
			}
		});
		
		JPanel headPanel = new JPanel();
		headPanel.setBackground(new Color(55, 170, 253, 25));
		headPanel.setBounds(0, 10, 256, 62);
		contentPane.add(headPanel);
		headPanel.setLayout(null);
		
		JLabel icon = new JLabel();
		ImageIcon image =  Login.user.getImageIcon();
		image.setImage(image.getImage().getScaledInstance(58,52,Image.SCALE_DEFAULT));
		icon.setIcon(image);
		icon.setBounds(27, 5, 58, 52);
		headPanel.add(icon);
		
		JLabel nickNameLabel = new JLabel(user.getNickname());
		nickNameLabel.setFont(new Font("楷体", Font.BOLD, 30));
		nickNameLabel.setBounds(106, 7, 109, 52);
		headPanel.add(nickNameLabel);
		setVisible(true);
	}

	private void addTray() { 
		JFrame _this = this;
		/*
         * 添加系统托盘
         */
        if (SystemTray.isSupported()) {
        	System.out.println("-------------------------------");
            // 获取当前平台的系统托盘
            SystemTray tray = SystemTray.getSystemTray();

            // 加载一个图片用于托盘图标的显示
            ImageIcon image = new ImageIcon(Main.class.getResource("/com/hxj/chat/common/statics/icon/QQ.png"));
System.out.println(image); 
            // 创建点击图标时的弹出菜单
            PopupMenu popupMenu = new PopupMenu();

            MenuItem openItem = new MenuItem("Open");
            MenuItem exitItem = new MenuItem("Exit");
            
            openItem.addActionListener(e->{
            	if(!this.isVisible()) {
            		this.setVisible(true);
            	}
            });
            exitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 点击退出菜单时退出程序
                    System.exit(0);
                }
            });

            popupMenu.add(openItem);
            popupMenu.add(exitItem);

            // 创建一个托盘图标
            TrayIcon trayIcon = new TrayIcon(image.getImage(), "myChat:"+Login.user.getNickname()+"("+Login.user.getId()+")", popupMenu);

            // 托盘图标自适应尺寸
            trayIcon.setImageAutoSize(true);

            trayIcon.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("托盘图标被右键点击");
                }
            });
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switch (e.getButton()) {
                        case MouseEvent.BUTTON1: {
                        	_this.setVisible(true);
                            System.out.println("托盘图标被鼠标左键被点击");
                            break;
                        }
                        case MouseEvent.BUTTON2: {
                            System.out.println("托盘图标被鼠标中键被点击");
                            break;
                        }
                        case MouseEvent.BUTTON3: {
                            System.out.println("托盘图标被鼠标右键被点击");
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            });

            // 添加托盘图标到系统托盘
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("当前系统不支持系统托盘");
        }
	}
}
