package com.hxj.chat.common.entity;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String nickname;
	private String password;
	private String icon;
	private Boolean sex;
	private Integer msgCount;
	private Boolean isOnline;
	private ImageIcon imageIcon;
	
	public ImageIcon getImageIcon() {
		return imageIcon;
	}


	public void setImageIcon(ImageIcon imageIcon) {
		this.imageIcon = imageIcon;
	}


	public Boolean getIsOnline() {
		return isOnline;
	}


	public void setIsOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}


	public Integer getMsgCount() {
		return msgCount;
	}


	public void setMsgCount(Integer msgCount) {
		this.msgCount = msgCount;
	}


	public User() {
		
	}
	
	
	public User(Integer id, String nickname, String password, String icon, Boolean sex) {
		super();
		this.id = id;
		this.nickname = nickname;
		this.password = password;
		this.icon = icon;
		this.sex = sex;
	}


	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Boolean getSex() {
		return sex;
	}
	public void setSex(Boolean sex) {
		this.sex = sex;
	}


	@Override
	public String toString() {
		return "User [id=" + id + ", nickname=" + nickname + ", password=" + password + ", icon=" + icon + ", sex="
				+ sex + "]";
	}
	
}
