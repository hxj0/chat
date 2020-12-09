package com.hxj.chat.server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtil {
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Integer update(String sql, Connection conn, Object... params){
		PreparedStatement prepareStatement;
		int count = 0;
		try {
			prepareStatement = conn.prepareStatement(sql);
			if(params != null) {
				for (int i = 0; i < params.length; i++) {
					prepareStatement.setObject(i+1, params[i]);
				}
			}
			count = prepareStatement.executeUpdate();
			JDBCUtil.closeResource(null, prepareStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/chat?Character=UTF-8&serverTimezone=UTC", "root", "123456");
	}
	public static void closeResource(ResultSet rs, Statement stmt){
        if( rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if( stmt != null){
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
	
	
}
