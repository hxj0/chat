package com.hxj.chat.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.hxj.chat.common.entity.Message;
import com.hxj.chat.common.entity.User;
import com.hxj.chat.server.util.JDBCUtil;

public class MessageDao {
	private Connection conn;

	public MessageDao(){
		try {
			conn = JDBCUtil.getConnection();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}
	
	public Boolean addMessage(Message msg) {
		String sql = "insert into message(fromId, toId, time, type, content, font) values(?, ?, ?, ?, ?, ?)";
		return JDBCUtil.update(sql, conn, new Object[] {msg.getFromId(), msg.getToId(), msg.getTime(), msg.getType(), msg.getContent(), msg.getFontFamily()}) > 0;
	}

	public List<Message> selectUndoMsg(Integer id) {
		List<Message> msgList = new ArrayList<>();
		String sql = "select * from message where toId=? and type != 1";
		try {
			PreparedStatement prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setObject(1, id);				
			ResultSet set = prepareStatement.executeQuery();
			while(set.next()) {
//				System.out.println(set);
				Timestamp timestamp = set.getTimestamp(4);
//				System.out.println(timestamp.getYear()+1900);
//				System.out.println(timestamp.getMonth()+1);
//				System.out.println(timestamp.getDate());
//				System.out.println(timestamp.getHours());
//				System.out.println(timestamp.getMinutes());
//				System.out.println(timestamp.getSeconds());
//				System.out.println(timestamp);
				msgList.add(new Message(set.getInt(2), set.getInt(3), 
//						LocalDateTime.of(year, month, dayOfMonth, hour, minute, second)
						LocalDateTime.of(timestamp.getYear()+1900, timestamp.getMonth()+1, timestamp.getDate(), timestamp.getHours(), timestamp.getMinutes(), timestamp.getSeconds())
						, set.getByte(6), set.getString(7), set.getString(5)));
			}
			JDBCUtil.closeResource(set, prepareStatement);
			String sql2 = "update message set type=1 where toId=? and type != 1";
			JDBCUtil.update(sql2, conn, new Object[] {id});
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return msgList;
	}

	public List<Message> selectMsgRecord(Integer fromId, Integer toId) {
		List<Message> msgList = new ArrayList<>(); 
		String sql = "select * from message where (fromId=? and toId = ?) or (fromId=? and toId = ?)  order by time asc";
		try {
			PreparedStatement prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setObject(1, fromId);				
			prepareStatement.setObject(2, toId);				
			prepareStatement.setObject(3, toId);				
			prepareStatement.setObject(4, fromId);				
			ResultSet set = prepareStatement.executeQuery();
			while(set.next()) {
				Timestamp timestamp = set.getTimestamp(4);
				msgList.add(new Message(set.getInt(2), set.getInt(3), 
						LocalDateTime.of(timestamp.getYear()+1900, timestamp.getMonth()+1, timestamp.getDate(), timestamp.getHours(), timestamp.getMinutes(), timestamp.getSeconds())
						, set.getByte(6), set.getString(7), set.getString(5)));
			}
			JDBCUtil.closeResource(set, prepareStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return msgList;
	}

	public List<Message> selectGroupMessage() {
		List<Message> msgList = new ArrayList<>(); 
		String sql = "select * from message where toId=0 order by time asc";
		try {
			PreparedStatement prepareStatement = conn.prepareStatement(sql);
			ResultSet set = prepareStatement.executeQuery();
			while(set.next()) {
				Timestamp timestamp = set.getTimestamp(4);
				msgList.add(new Message(set.getInt(2), set.getInt(3), 
						LocalDateTime.of(timestamp.getYear()+1900, timestamp.getMonth()+1, timestamp.getDate(), timestamp.getHours(), timestamp.getMinutes(), timestamp.getSeconds())
						, set.getByte(6), set.getString(7), set.getString(5)));
			}
			JDBCUtil.closeResource(set, prepareStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return msgList;
	}
}
