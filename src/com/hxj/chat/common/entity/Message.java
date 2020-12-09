package com.hxj.chat.common.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable{
	private Integer id;
	private Integer fromId;
	private Integer toId;
	private LocalDateTime time;
	private Byte type;
	private String content;
	private String fontFamily;
	
	public Message() {
		
	}
	
	public Message(Message message) {
		this.fromId = message.fromId;
		this.toId = message.toId;
		this.time = message.time;
		this.type = message.type;
		this.content = message.content;
	}
	
	public Message(int fromId, int toId, LocalDateTime time, byte type, String content, String fontFamily) {
		this.fromId = fromId;
		this.toId = toId;
		this.time = time;
		this.type = type;
		this.content = content;
		this.fontFamily = fontFamily;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getFromId() {
		return fromId;
	}
	public void setFromId(Integer fromId) {
		this.fromId = fromId;
	}
	public Integer getToId() {
		return toId;
	}
	public void setToId(Integer toId) {
		this.toId = toId;
	}
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) { 
		this.time = time;
	}
	public Byte getType() {
		return type;
	}
	public void setType(Byte type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", fromId=" + fromId + ", toId=" + toId + ", time=" + time + ", type=" + type
				+ ", content=" + content + ", fontFamily=" + fontFamily + "]";
	}
	
}
