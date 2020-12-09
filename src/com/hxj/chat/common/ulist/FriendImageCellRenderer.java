package com.hxj.chat.common.ulist;



import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingConstants;

import com.hxj.chat.common.entity.User;


/**
 * DefaultListCellRenderer是JList的渲染器，它继承了JLabel
 * 所以本类可以看成一个JLabel
 */
public class FriendImageCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 6686996369807205938L;

	/**
	 * list：JList对象
	 * value：重点，它就是模型数据
	 * index：当前选择的单元格下标，从0开始
	 * isSelected：单元格选中的状态，你选择A之后再选择A，它返回false
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, 
			int index, boolean isSelected, boolean cellHasFocus) {
		
		if(value instanceof User) {
			User user = (User)value;
			String uiconPath = user.getIcon();
			String userName = user.getNickname();
			String isOnline = user.getIsOnline()? "在线":"离线";
			ImageIcon icon = new ImageIcon(uiconPath);
			icon.setImage(icon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
			setIcon(icon);
			int msgCount = user.getMsgCount() == null? 0 : user.getMsgCount();
			String text = "<html><body>"
					+ "<div color='black' style='font-size:15px;margin-top:8px; margin-left:10px;'>"
					+ "<span style=''>"+userName+"</span>&nbsp;&nbsp;"
					+ "<span style='display:inline; margin-left:10px;'>["+isOnline+"]</span>&nbsp;&nbsp;";
			if(user.getMsgCount() != null && user.getMsgCount() != 0) {
				text += "<span style='margin-left:10px; color:red; border-radius:50%'>"+user.getMsgCount()+"</span></div></body></html>";
			}else text += "</div></body></html>";
			//这个setTtext方法支持html语句
			setText(text);
			if(isSelected)setBackground(new Color(155, 155, 155, 70));
			else setBackground(Color.WHITE);
			if(!user.getIsOnline())setBackground(new Color(200, 200, 200, 180));
			setVerticalTextPosition(SwingConstants.TOP);
			setHorizontalTextPosition(SwingConstants.RIGHT);
		}
		return this;
	}
}
