package com.hxj.chat.client.frame;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.hxj.chat.common.entity.Message;
import com.hxj.chat.common.entity.User;
import com.hxj.chat.common.enums.MsgType;
import com.hxj.chat.common.ulist.ImageCellRenderer;
import com.hxj.chat.common.ulist.ImageListModel;

public class Find extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private JPanel contentPane;
	private JTextField textField;
	
	public static JList<User> list;
 

	/**
	 * Create the frame.
	 */
	public Find() {

		setIconImage(Toolkit.getDefaultToolkit().getImage(Find.class.getResource("/com/hxj/chat/common/statics/icon/搜索--1.png")));
		setTitle("查找");
		setBounds(100, 100, 497, 373);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("群聊");
		chckbxNewCheckBox.setFont(new Font("����", Font.PLAIN, 14));
		chckbxNewCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		chckbxNewCheckBox.setBounds(202, 92, 109, 23);
		contentPane.add(chckbxNewCheckBox);
		
		JCheckBox chckbxNewCheckBox_1 = new JCheckBox("账号");
		chckbxNewCheckBox_1.setHorizontalAlignment(SwingConstants.CENTER);
		chckbxNewCheckBox_1.setFont(new Font("����", Font.PLAIN, 14));
		chckbxNewCheckBox_1.setBounds(69, 92, 109, 23);
		contentPane.add(chckbxNewCheckBox_1);
		chckbxNewCheckBox_1.setSelected(true);
		
		textField = new JTextField();
		textField.setBounds(136, 43, 191, 31);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("账号/群号");
		lblNewLabel.setIcon(new ImageIcon(Find.class.getResource("/com/hxj/chat/common/statics/icon/账号.png")));
		lblNewLabel.setFont(new Font("新宋体", Font.PLAIN, 16));
		lblNewLabel.setBounds(34, 46, 92, 23);
		contentPane.add(lblNewLabel);
		

		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(54, 121, 399, 160);
		contentPane.add(scrollPane);
		
		list = new JList<>();
		ImageListModel model = new ImageListModel() ;
		list.setModel(model);
		list.setCellRenderer(new ImageCellRenderer());
		scrollPane.setViewportView(list);
		
		JButton btnNewButton = new JButton("查找");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = textField.getText();
				for (int i = 0; i < text.length(); i++) {
					if(text.charAt(i) < '0' || text.charAt(i) > 'a') {
						JOptionPane.showMessageDialog(null, "不能含有非数字字符且数字不大于六位！","提示", JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("likeId", text);
				map.put("msgType", MsgType.FIND);
				try {
					Login.oos.writeObject(map); 
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setFont(new Font("宋体", Font.PLAIN, 13));
		btnNewButton.setIcon(new ImageIcon(Find.class.getResource("/com/hxj/chat/common/statics/icon/查找.png")));
		btnNewButton.setBounds(347, 43, 92, 31);
		contentPane.add(btnNewButton);
		
		JButton addBtn = new JButton("添加");
		addBtn.setFont(new Font("宋体", Font.PLAIN, 13));
		addBtn.setIcon(new ImageIcon(Find.class.getResource("/com/hxj/chat/common/statics/icon/添加.png")));
		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(list.getSelectedValue() != null) {
					Map<String,Object> map = new HashMap<String, Object>();
					map.put("msgType", MsgType.ADDFRIEND);
					Message msg = new Message(Login.user.getId(), list.getSelectedValue().getId(), LocalDateTime.now(), (byte)10, "", "楷体");
					map.put("message", msg);
					System.out.println(map);
					try {
						Login.oos.writeObject(map); 
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					JOptionPane.showMessageDialog(null, "好友申请已发送！","提示", JOptionPane.PLAIN_MESSAGE,
							new ImageIcon(Find.class.getResource("/com/hxj/chat/common/statics/icon/添加.png")));
				}
			}
		});
		addBtn.setBounds(360, 291, 93, 35);
		contentPane.add(addBtn);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false); 
			}
		});
	}
}
