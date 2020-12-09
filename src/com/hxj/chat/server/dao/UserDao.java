package com.hxj.chat.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hxj.chat.common.entity.User;
import com.hxj.chat.server.util.JDBCUtil;

public class UserDao {
	
	private Connection conn;
	
	public UserDao(){
		try {
			conn = JDBCUtil.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println(new UserDao().getFriends(123456));
		System.out.println(new UserDao().selectAll());
	}
	
	public User selectOne(Integer id, String password) { 
		User user = null;
		String sql = "select * from user where id = ? and password=? limit 1";
		try {
			PreparedStatement prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setObject(1, id);
			prepareStatement.setObject(2, password);
			ResultSet set = prepareStatement.executeQuery();
			while(set.next()) {
				user = new User(set.getInt(1), set.getString(2), set.getString(3), set.getString(4), set.getBoolean(5));
			}
			JDBCUtil.closeResource(set, prepareStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public List<User> selectByLikeId(String id, Integer hostId) {
		List<User> users = new ArrayList<>();
		String sql = "select * from user where id like ? and id not in (select friendId from friend where hostId = ?) and id != ?";
		try {
			PreparedStatement prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setObject(1, "%"+id+"%");		
			prepareStatement.setObject(2, hostId);			
			prepareStatement.setObject(3, hostId);			
			ResultSet set = prepareStatement.executeQuery();
			while(set.next()) {
				users.add(new User(set.getInt(1), set.getString(2), set.getString(3), set.getString(4), set.getBoolean(5)));
			}
			JDBCUtil.closeResource(set, prepareStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	public User selectById(Integer id) { 
		User user = null;
		String sql = "select * from user where id = ? limit 1";
		try {
			PreparedStatement prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setObject(1, id);
			ResultSet set = prepareStatement.executeQuery();
			while(set.next()) {
				user = new User(set.getInt(1), set.getString(2), set.getString(3), set.getString(4), set.getBoolean(5));
			}
			JDBCUtil.closeResource(set, prepareStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public Boolean addUser(User user) {
		String sql = "insert into user values(?, ?, ?, ?, ?)";
		return JDBCUtil.update(sql, conn, new Object[] {user.getId(), user.getNickname(), user.getPassword(), user.getIcon(), user.getSex()}) > 0;
	}

	public List<User> selectAll() {
		List<User> users = new ArrayList<>();
		String sql = "select * from user";
		try {
			PreparedStatement prepareStatement = conn.prepareStatement(sql);
			ResultSet set = prepareStatement.executeQuery();
			while(set.next()) {
				users.add(new User(set.getInt(1), set.getString(2), null, set.getString(4), set.getBoolean(5)));
			}
			JDBCUtil.closeResource(set, prepareStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return users;
	}

	
	public Boolean deleteByPrimrayKey(Integer id) { 
		String sql = "delete from user where id = ?";
		return JDBCUtil.update(sql, conn, new Object[] {id}) > 0;
	}


	public Boolean update(User t) {
		String sql = "update user set nickname=?, password=?, icon=?, sex=? where id = ?";
		return JDBCUtil.update(sql, conn, new Object[]{t.getNickname(), t.getPassword(), t.getIcon(), t.getSex(), t.getId()}) > 0;
	}

	public List<User> getFriends(Integer id) {
		String sql = "select * from user where id in (select friendId from friend where hostId = ?)";
		List<User> users = new ArrayList<>();
		try {
			PreparedStatement prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setInt(1, id);
			ResultSet set = prepareStatement.executeQuery();
			while(set.next()) {
				users.add(new User(set.getInt(1), set.getString(2), set.getString(3), set.getString(4), set.getBoolean(5)));
			}
			JDBCUtil.closeResource(set, prepareStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}
	
	public Boolean confirmMsg(Integer from, Integer to) {
		String sql = "update message set type=? where fromId=? and toId=? and type=10";
		return JDBCUtil.update(sql, conn, new Object[]{1, from, to}) > 0;
	}
	
	public Boolean addFriend(Integer from, Integer to) {
		String sql = "insert into friend values(?, ?), (?, ?)";
		return JDBCUtil.update(sql, conn, new Object[]{from, to, to, from}) > 0 && confirmMsg(from, to);
	}

	public Boolean deleteFriend(Integer id,Integer friendId) {
		String sql = "delete from friend where (hostId = ? and friendId=?) or (hostId = ? and friendId=?)";
		return JDBCUtil.update(sql, conn, new Object[]{id, friendId, friendId, id}) > 0;
	}
	
}
