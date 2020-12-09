package com.hxj.chat.common.ulist;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import com.hxj.chat.common.entity.User;


/**
 * JList的模型类，构成JList的模型元素
 * 这个模型类，最终是提供给某一个类去调用
 * @author Ron
 *
 */
public class ImageListModel extends AbstractListModel<User>{

	private static final long serialVersionUID = -8573230159537206489L;
	
	//为了配合某一个类去调用，所以我们得返回相应的类型
	private final List<User> list = new ArrayList<>();
	
	//是我们自己提供的
	public void addElement(User str) {
		list.add(str);
	}
	
	public void clear() {
		list.clear();
	}
	
	public void addAll(List<User> users) {
		list.addAll(users);
	}
	
	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public User getElementAt(int index) {
		return list.get(index);
	}
	
}
