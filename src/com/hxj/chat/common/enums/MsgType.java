package com.hxj.chat.common.enums;

public enum MsgType {
	REGISTRY(1,"注册消息"),
	LOGIN(2 , "登录消息"),
	UN_LOGIN(3, "好友离线提醒"),
	CHAT(4, "聊天消息"),
	DD(5,"抖动消息"),
	ULIST(6,"在线用户列表"),
	FIND(7, "查找用户/群聊"),
	SEND_FILE(8,"发送文件"),
	ADDFRIEND(9,"添加好友"),
	ADDFRIEND_RESULT(10,"添加结果"), 
	DELETE_FRIEND(11,"删除好友"), 
	CHAT_RECORD(12,"删除好友"), 
	GROUP_CHAT(12,"群聊消息"),
	ALL_USER(13,"所有用户"),
	GROUP_CHAT_RECORD(14,"群聊消息记录"),;
	
	private Integer status;
	private String desc;
	MsgType(int status , String desc) {
		this.status = status;
		this.desc = desc;
	}
}
