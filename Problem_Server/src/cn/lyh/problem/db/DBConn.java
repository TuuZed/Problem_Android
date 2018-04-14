package cn.lyh.problem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class DBConn extends Tools {

	Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;

	Connection getConn() {
		Connection conn = null;
		String host = "115.238.250.76";
		String port = "3306";
		String db = "sq_i1563456";
		String user = "sq_i1563456";
		String password = "red338";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			final String url = "jdbc:mysql://" + host + ":" + port + "/" + db
					+ "?user=" + user + "&password=" + password;
			conn = DriverManager.getConnection(url);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	void close(ResultSet rs, Statement ps, Connection conn) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (ps != null) {
				ps.close();
				ps = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
