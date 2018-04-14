package cn.lyh.problem.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DB extends DBConn {

	private int count = 20;

	private DB() {
	}

	private static DB db = new DB();

	public static DB getDB() {
		return db;
	}

	/**
	 * 注册
	 * 
	 * @param name
	 * @param email
	 * @param passwd
	 * @return
	 */
	public JSONObject register(String name, String email, String passwd) {
		conn = getConn();
		JSONObject object = new JSONObject();
		try {
			// 查找账户是否存在，存在则不允许注册，不存在则注册
			ps = conn.prepareStatement("SELECT *FROM t_user WHERE u_email=?");
			ps.setString(1, email);
			rs = ps.executeQuery();
			if (rs.next()) {
				object = error("该邮箱已注册!");
				return object;
			}
			ps = conn
					.prepareStatement("INSERT INTO t_user (u_name,u_email,u_sex,u_passwd) VALUES (?,?,?,?)");
			ps.setString(1, name);
			ps.setString(2, email);
			ps.setString(3, encode("男"));
			ps.setString(4, passwd);
			int i = ps.executeUpdate();
			if (i != 0) {
				// 注册成功
				object = login(email, passwd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			object = error("注册失败");
		} finally {
			close(rs, ps, conn);
		}
		return object;
	}

	/**
	 * 登入
	 * 
	 * @param email
	 * @param passwd
	 * @return
	 */
	public JSONObject login(String email, String passwd) {
		JSONObject object = new JSONObject();
		conn = getConn();
		try {
			ps = conn
					.prepareStatement("SELECT *FROM t_user WHERE u_email=? AND u_passwd=?");
			ps.setString(1, email);
			ps.setString(2, passwd);
			rs = ps.executeQuery();
			if (rs.next()) {
				// 读取用户数据
				try {
					object.put("code", 100);
					JSONObject userinfo = new JSONObject();
					userinfo.put("uid", Integer.parseInt(rs.getString(1)));
					userinfo.put("name", decode(rs.getString(2)));
					userinfo.put("sex", decode(rs.getString(3)));
					userinfo.put("email", decode(rs.getString(4)));
					userinfo.put("passwd", decode(rs.getString(5)));
					try {
						userinfo.put("intro", decode(rs.getString(6)));
					} catch (Exception e) {
						userinfo.put("intro", "");
					}
					userinfo.put("date", rs.getString(7));
					object.put("userinfo", userinfo);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				object = error(" 账户或密码错误");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			object = error("error");
		} finally {
			close(rs, ps, conn);
		}
		return object;
	}

	/**
	 * 更新个人信息
	 * 
	 * @param uid
	 * @param name
	 * @param sex
	 * @param intro
	 * @return
	 */
	public JSONObject updataUserInfo(int uid, String name, String sex,
			String intro) {
		JSONObject object = new JSONObject();
		conn = getConn();
		try {
			ps = conn
					.prepareStatement("UPDATE t_user SET u_name=?,u_sex=?,u_intro=? WHERE u_id=?");
			ps.setString(1, name);
			ps.setString(2, sex);
			ps.setString(3, intro);
			ps.setInt(4, uid);
			int i = ps.executeUpdate();
			if (i == 0) {
				object = error("修改失败");
			} else {
				// 修改成功
				ps = conn
						.prepareStatement("SELECT u_email,u_passwd FROM t_user WHERE u_id=?");
				ps.setInt(1, uid);
				rs = ps.executeQuery();
				if (rs.next()) {
					object = login(rs.getString(1), rs.getString(2));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}
		return object;
	}

	/**
	 * 提问
	 * 
	 * @param uid
	 * @param problem
	 * @param topic
	 * @param explain
	 * @return
	 */
	public JSONObject quiz(int uid, String problem, String topic, String explain) {
		JSONObject object = new JSONObject();
		conn = getConn();
		int tid = 0;
		try {
			ps = conn
					.prepareStatement("SELECT t_id FROM t_topic WHERE t_topic=?");
			ps.setString(1, topic);
			rs = ps.executeQuery();
			if (rs.next()) {
				// 话题已存在
				tid = rs.getInt(1);
			} else {
				ps = conn
						.prepareStatement("INSERT INTO t_topic (t_topic) VALUES (?)");
				ps.setString(1, topic);
				int i = ps.executeUpdate();
				if (i != 0) {
					ps = conn
							.prepareStatement("SELECT t_id FROM t_topic WHERE t_topic=?");
					ps.setString(1, topic);
					rs = ps.executeQuery();
					rs.next();
					tid = rs.getInt(1);
				}
			}
			// 判断问题是否存在，存在则不允许创建
			ps = conn
					.prepareStatement("SELECT p_id FROM t_problem WHERE p_problem=?");
			ps.setString(1, problem);
			rs = ps.executeQuery();
			if (rs.next()) {
				// 存在
				object = error("问题已存在");
				return object;
			}
			// 创建问题
			if (explain == null) {
				explain = "";
			}
			ps = conn
					.prepareStatement("INSERT INTO t_problem (u_id,t_id,p_problem,p_explain) VALUES (?,?,?,?)");
			ps.setInt(1, uid);
			ps.setInt(2, tid);
			ps.setString(3, problem);
			ps.setString(4, explain);
			int i = ps.executeUpdate();
			if (i != 0) {
				object = success("提问成功");
			} else {
				object = error("提问失败");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}
		return object;
	}

	/**
	 * 更新提问
	 * 
	 * @param uid
	 * @param pid
	 * @param topic
	 * @param explain
	 * @return
	 */
	public JSONObject updataQuiz(int pid, String topic, String explain) {
		int tid = 0;
		JSONObject object = new JSONObject();
		conn = getConn();
		try {
			ps = conn
					.prepareStatement("SELECT t_id FROM t_topic WHERE t_topic=?");
			ps.setString(1, topic);
			rs = ps.executeQuery();
			if (rs.next()) {
				// 话题已存在
				tid = rs.getInt(1);
			} else {
				ps = conn
						.prepareStatement("INSERT INTO t_topic (t_topic) VALUES (?)");
				ps.setString(1, topic);
				int i = ps.executeUpdate();
				if (i != 0) {
					ps = conn
							.prepareStatement("SELECT t_id FROM t_topic WHERE t_topic=?");
					ps.setString(1, topic);
					rs = ps.executeQuery();
					rs.next();
					tid = rs.getInt(1);
				}
			}
			// 修改问题
			if (explain == null) {
				explain = "";
			}
			ps = conn
					.prepareStatement("UPDATE t_problem SET t_id = ?,p_explain=? WHERE p_id=?");
			ps.setInt(1, tid);
			ps.setString(2, explain);
			ps.setInt(3, pid);

			int i = ps.executeUpdate();
			if (i != 0) {
				object = success("修改提问成功");
			} else {
				object = error("修改提问失败");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}
		return object;
	}

	/**
	 * 回答
	 * 
	 * @param uid
	 * @param pid
	 * @param reply
	 * @return
	 */
	public JSONObject reply(int uid, int pid, String reply) {
		JSONObject object = new JSONObject();
		conn = getConn();
		try {
			ps = conn
					.prepareStatement("INSERT INTO t_reply(p_id,u_id,r_reply) VALUES (?,?,?)");
			ps.setInt(1, pid);
			ps.setInt(2, uid);
			ps.setString(3, reply);
			int i = ps.executeUpdate();
			if (i != 0) {
				object = success("回答成功");
			} else {
				object = error("回答失败");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}
		return object;
	}

	/**
	 * 更新回答
	 * 
	 * @param rid
	 * @param reply
	 * @return
	 */
	public JSONObject updataReply(int rid, String reply) {
		JSONObject object = new JSONObject();
		conn = getConn();
		try {
			ps = conn
					.prepareStatement("UPDATE t_reply SET r_reply=? WHERE  r_id=?");
			ps.setString(1, reply);
			ps.setInt(2, rid);
			int i = ps.executeUpdate();
			if (i != 0) {
				object = success("修改回答成功");
			} else {
				object = error("修改回答失败");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}
		return object;
	}

	/**
	 * 点赞
	 * 
	 * @param uid
	 * @param rid
	 * @param objUid
	 * @return
	 */
	public JSONObject praise(int uid, int rid) {
		JSONObject object = new JSONObject();
		conn = getConn();
		try {
			// 判断该用户是否已经点过赞了
			ps = conn
					.prepareStatement("SELECT *FROM t_praise WHERE u_id=? AND r_id=?");
			ps.setInt(1, uid);
			ps.setInt(2, rid);
			rs = ps.executeQuery();
			if (rs.next()) {
				// 已经点赞
				return error("已经赞过了");
			}
			ps = conn
					.prepareStatement("INSERT INTO t_praise(r_id,u_id) VALUES (?,?)");
			ps.setInt(1, rid);
			ps.setInt(2, uid);
			int i = ps.executeUpdate();
			if (i != 0) {
				object = success("点赞成功");
			} else {
				object = error("点赞失败");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			object = error("点赞失败");
		} finally {
			close(rs, ps, conn);
		}
		return object;
	}

	/**
	 * 收藏
	 * 
	 * @param uid
	 * @param rid
	 * @return
	 */
	public JSONObject enshrine(int uid, int rid) {
		JSONObject object = new JSONObject();
		conn = getConn();
		try {
			// 判断该用户是否已经收藏过
			ps = conn
					.prepareStatement("SELECT *FROM t_enshrine WHERE u_id=? AND r_id=?");
			ps.setInt(1, uid);
			ps.setInt(2, rid);
			rs = ps.executeQuery();
			if (rs.next()) {
				// 已经收藏
				return error("已经收藏过了");
			}
			ps = conn
					.prepareStatement("INSERT INTO t_enshrine(r_id,u_id)VALUES(?,?)");
			ps.setInt(1, rid);
			ps.setInt(2, uid);
			int i = ps.executeUpdate();
			if (i == 0) {
				object = error("收藏失败");
			} else {
				object = success("收藏成功");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}
		return object;
	}

	/**
	 * 移除收藏
	 * 
	 * @param uid
	 * @param rid
	 * @return
	 */
	public JSONObject unEnshrine(int uid, int rid) {
		conn = getConn();
		JSONObject object = new JSONObject();
		try {
			ps = conn
					.prepareStatement("DELETE FROM t_enshrine WHERE u_id=? AND r_id=?");
			ps.setInt(1, uid);
			ps.setInt(2, rid);
			int i = ps.executeUpdate();
			if (i == 0) {
				object = error("移除收藏失败");
			} else {
				object = success("移除收藏成功");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}
		return object;
	}

	/**
	 * 取得指定用户答过，问过，被赞的数量
	 * 
	 * @param uid
	 * @return
	 */
	public JSONObject getCount(int uid) {
		JSONObject object = new JSONObject();
		conn = getConn();
		int praise = 0;
		int problem = 0;
		int reply = 0;
		try {
			ps = conn
					.prepareStatement("SELECT COUNT(*) FROM t_praise WHERE u_id = ?");
			ps.setInt(1, uid);
			rs = ps.executeQuery();
			if (rs.next()) {
				praise = rs.getInt(1);
			}
			ps = conn
					.prepareStatement("SELECT COUNT(*) FROM t_problem WHERE u_id = ?");
			ps.setInt(1, uid);
			rs = ps.executeQuery();
			if (rs.next()) {
				problem = rs.getInt(1);
			}
			ps = conn
					.prepareStatement("SELECT COUNT(*) FROM t_reply WHERE u_id = ?");
			ps.setInt(1, uid);
			rs = ps.executeQuery();
			if (rs.next()) {
				reply = rs.getInt(1);
			}
			try {
				object.put("code", 100);
				object.put("praise", praise);
				object.put("problem", problem);
				object.put("reply", reply);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}
		return object;
	}

	/**
	 * 取得指定用户问过的问题
	 * 
	 * @param uid
	 * @return
	 */
	public JSONObject uidQuiz(int uid, int page) {
		conn = getConn();
		JSONObject object = new JSONObject();
		try {
			ps = conn
					.prepareStatement("SELECT p_id,p_problem,t_id,p_explain FROM t_problem WHERE u_id=? ORDER BY p_date DESC");
			ps.setInt(1, uid);
			rs = ps.executeQuery();
			for (int i = 0; i < (page - 1) * count; i++) {
				if (!rs.next()) {
					return error("长度超出了");
				}
			}
			JSONArray array = new JSONArray();
			for (int i = 0; i < count; i++) {
				if (rs.next()) {
					JSONObject object2 = new JSONObject();
					int pid = rs.getInt(1);
					String problem = rs.getString(2);
					int tid = rs.getInt(3);
					String explain = rs.getString(4)+"";
					
					try {
						object2.put("pid", pid);
						object2.put("problem", decode(problem));
						object2.put("explain", decode(explain));
						object2.put("tid", tid);
						//话题
						ps = conn
								.prepareStatement("SELECT t_topic FROM t_topic WHERE t_id=?");
						ps.setInt(1, tid);
						ResultSet rs2 = ps.executeQuery();
						if (rs2.next()) {
							object2.put("topic", decode(rs2.getString(1)));
						}
						object.put("last", false);
						array.put(i, object2);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					try {
						object.put("last", true);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			}

			try {
				object.put("code", 100);
				object.put("problems", array);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}

		return object;
	}

	/**
	 * 取得指定用户答过的问题
	 * 
	 * @param uid
	 * @param page
	 * @return
	 */
	public JSONObject uidReply(int uid, int page) {
		JSONObject object = new JSONObject();
		conn = getConn();
		try {
			ps = conn
					.prepareStatement("SELECT *FROM t_reply WHERE u_id=? ORDER BY r_date DESC");
			ps.setInt(1, uid);
			rs = ps.executeQuery();
			for (int i = 0; i < (page - 1) * count; i++) {
				if (!rs.next()) {
					return error("长度超出了");
				}
			}
			JSONArray array = new JSONArray();
			for (int i = 0; i < count; i++) {
				if (rs.next()) {
					JSONObject object2 = new JSONObject();
					int rid = rs.getInt(1);
					int pid = rs.getInt(2);
					String reply = rs.getString(4);
					// 赞
					ps = conn
							.prepareStatement("SELECT count(*) FROM t_praise WHERE r_id=?");
					ps.setInt(1, rid);
					ResultSet rs2 = ps.executeQuery();
					rs2.next();
					int praise = rs2.getInt(1);
					// 问题
					ps = conn
							.prepareStatement("SELECT p_problem FROM t_problem WHERE p_id=?");
					ps.setInt(1, pid);
					rs2 = ps.executeQuery();
					rs2.next();
					String problem = rs2.getString(1);

					try {
						object2.put("rid", rid);
						object2.put("reply", decode(reply));
						object2.put("pid", pid);
						object2.put("problem", decode(problem));
						object2.put("praise", praise);
						object.put("last", false);
						array.put(i, object2);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				} else {
					try {
						object.put("last", true);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			}

			try {
				object.put("code", 100);
				object.put("replys", array);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}

		return object;
	}

	/**
	 * 取得指定用户的收藏列表
	 * 
	 * @param uid
	 * @param page
	 * @return
	 */
	public JSONObject uidEnshrine(int uid, int page) {
		JSONObject object = new JSONObject();
		conn = getConn();
		try {
			ps = conn
					.prepareStatement("SELECT r_id FROM t_enshrine WHERE u_id=?");
			ps.setInt(1, uid);
			rs = ps.executeQuery();

			for (int i = 0; i < (page - 1) * count; i++) {
				if (!rs.next()) {
					return error("长度超出了");
				}
			}
			JSONArray array = new JSONArray();
			for (int i = 0; i < count; i++) {
				if (rs.next()) {
					JSONObject object2 = new JSONObject();
					int rid = rs.getInt(1);
					ps = conn
							.prepareStatement("SELECT p_id,r_reply,u_id FROM t_reply WHERE r_id=? ORDER BY r_date DESC");
					ps.setInt(1, rid);
					ResultSet rs2 = ps.executeQuery();
					if (rs2.next()) {
						int pid = rs2.getInt(1);
						String reply = (rs2.getString(2));
						int ruid = rs2.getInt(3);
						// 问题
						ps = conn
								.prepareStatement("SELECT p_problem,t_id FROM t_problem WHERE p_id=?");
						ps.setInt(1, pid);
						rs2 = ps.executeQuery();
						rs2.next();
						int tid = rs2.getInt(2);
						String problem = rs2.getString(1);
						// 话题
						ps = conn
								.prepareStatement("SELECT t_topic,t_id FROM t_topic WHERE t_id=? ");
						ps.setInt(1, tid);
						rs2 = ps.executeQuery();
						rs2.next();
						String topic = rs2.getString(1);
						// 名字、性别
						ps = conn
								.prepareStatement("SELECT u_name,u_sex FROM t_user WHERE u_id=?");
						ps.setInt(1, ruid);
						rs2 = ps.executeQuery();
						rs2.next();
						String name = rs2.getString(1);
						String sex = rs2.getString(2);
						// 赞
						ps = conn
								.prepareStatement("SELECT count(*) FROM t_praise WHERE r_id=?");
						ps.setInt(1, rid);
						rs2 = ps.executeQuery();
						rs2.next();
						int praise = rs2.getInt(1);
						close(rs2, null, null);
						try {
							object2.put("rid", rid);
							object2.put("reply", decode(reply));
							object2.put("tid", tid);
							object2.put("topic", decode(topic));
							object2.put("pid", pid);
							object2.put("problem", decode(problem));
							object2.put("praise", praise);
							object2.put("uid", ruid);
							object2.put("name", decode(name));
							object2.put("sex", decode(sex));
							object.put("last", false);
							array.put(i, object2);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						object.put("last", true);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			}

			try {
				object.put("code", 100);
				object.put("enshrines", array);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}
		return object;
	}

	/**
	 * 取得指定话题至少有一个答案的问题列表
	 * 
	 * @param tid
	 * @param page
	 * @return
	 */
	public JSONObject tidQuizAt(int tid, int page) {
		conn = getConn();
		JSONObject object = new JSONObject();
		try {
			ps = conn.prepareStatement("SELECT *FROM t_problem WHERE t_id =?");
			ps.setInt(1, tid);
			rs = ps.executeQuery();
			for (int i = 0; i < (page - 1) * count; i++) {
				if (!rs.next()) {
					return error("长度超出了");
				}
			}
			JSONArray array = new JSONArray();
			for (int i = 0; i < count; i++) {
				if (rs.next()) {
					JSONObject object2 = new JSONObject();
					int pid = rs.getInt(1);
					String problem = rs.getString(4);
					ps = conn
							.prepareStatement("SELECT *FROM t_reply WHERE p_id = ?  ORDER BY r_date DESC");
					ps.setInt(1, pid);
					ResultSet rs2 = ps.executeQuery();

					if (rs2.next()) {
						int rid = rs2.getInt(1);
						int uid = rs2.getInt(3);
						String reply = rs2.getString(4);
						// 名字、性别
						ps = conn
								.prepareStatement("SELECT u_name,u_sex FROM t_user WHERE u_id = ?");
						ps.setInt(1, uid);
						rs2 = ps.executeQuery();
						rs2.next();
						String name = rs2.getString(1);
						String sex = rs2.getString(2);
						// 点赞数量
						ps = conn
								.prepareStatement("SELECT COUNT(*) FROM t_praise WHERE r_id = ?");
						ps.setInt(1, rid);
						rs2 = ps.executeQuery();
						rs2.next();
						int praise = rs2.getInt(1);
						try {
							object2.put("pid", pid);
							object2.put("problem", decode(problem));
							object2.put("rid", rid);
							object2.put("reply", decode(reply));
							object2.put("uid", uid);
							object2.put("name", decode(name));
							object2.put("sex", decode(sex));
							object2.put("praise", praise);
							object.put("last", false);
							array.put(i, object2);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						object.put("last", true);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			}
			try {
				object.put("code", 100);
				object.put("problems", array);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}

		return object;
	}

	/**
	 * 取得全部至少有一个答案问题列表
	 * 
	 * @return
	 */
	public JSONObject allQuizAt(int page) {
		conn = getConn();
		JSONObject object = new JSONObject();
		try {
			ps = conn
					.prepareStatement("SELECT *FROM t_problem  ORDER BY p_date DESC");
			rs = ps.executeQuery();
			for (int i = 0; i < (page - 1) * count; i++) {
				if (!rs.next()) {
					return error("长度超出了");
				}
			}
			JSONArray array = new JSONArray();
			for (int i = 0; i < count; i++) {
				if (rs.next()) {
					JSONObject object2 = new JSONObject();
					int pid = rs.getInt(1);
					int tid = rs.getInt(3);
					String problem = rs.getString(4);
					ps = conn
							.prepareStatement("SELECT *FROM t_reply WHERE p_id = ?  ORDER BY r_date DESC");
					ps.setInt(1, pid);
					ResultSet rs2 = ps.executeQuery();

					if (rs2.next()) {
						int rid = rs2.getInt(1);
						int uid = rs2.getInt(3);
						String reply = rs2.getString(4);

						// 话题
						ps = conn
								.prepareStatement("SELECT t_topic FROM t_topic WHERE t_id = ?");
						ps.setInt(1, tid);
						rs2 = ps.executeQuery();
						rs2.next();
						String topic = rs2.getString(1);
						// 名字
						ps = conn
								.prepareStatement("SELECT u_name,u_sex FROM t_user WHERE u_id = ?");
						ps.setInt(1, uid);
						rs2 = ps.executeQuery();
						rs2.next();
						String name = rs2.getString(1);
						String sex = rs2.getString(2);
						// 点赞数量
						ps = conn
								.prepareStatement("SELECT COUNT(*) FROM t_praise WHERE r_id = ?");
						ps.setInt(1, rid);
						rs2 = ps.executeQuery();
						rs2.next();
						int praise = rs2.getInt(1);
						try {
							object2.put("pid", pid);
							object2.put("problem", decode(problem));
							object2.put("rid", rid);
							object2.put("reply", decode(reply));
							object2.put("tid", tid);
							object2.put("topic", decode(topic));
							object2.put("uid", uid);
							object2.put("name", decode(name));
							object2.put("sex", decode(sex));
							object2.put("praise", praise);
							if (!(object2.toString().equals("null") || object2
									.toString() == null)) {
								array.put(i, object2);
							}
							object.put("last", false);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						object.put("last", true);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			}
			try {
				object.put("code", 100);
				object.put("problems", array);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}

		return object;
	}

	/**
	 * 取得全部问题列表
	 * 
	 * @param page
	 * @return
	 */
	public JSONObject allQuiz(int page) {
		conn = getConn();
		JSONObject object = new JSONObject();
		try {
			ps = conn
					.prepareStatement("SELECT *FROM t_problem  ORDER BY p_date DESC");
			rs = ps.executeQuery();
			for (int i = 0; i < (page - 1) * count; i++) {
				if (!rs.next()) {
					return error("长度超出了");
				}
			}
			JSONArray array = new JSONArray();
			for (int i = 0; i < count; i++) {
				if (rs.next()) {
					JSONObject object2 = new JSONObject();
					int pid = rs.getInt(1);
					int tid = rs.getInt(3);
					String problem = rs.getString(4);
					try {
						object2.put("pid", pid);
						object2.put("problem", decode(problem));
						object2.put("tid", tid);

						ps = conn
								.prepareStatement("SELECT t_topic FROM t_topic WHERE t_id=?");
						ps.setInt(1, tid);
						ResultSet rs2 = ps.executeQuery();
						rs2.next();
						object2.put("topic", decode(rs2.getString(1)));
						object.put("last", false);
						array.put(i, object2);
						close(rs2, null, null);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					try {
						object.put("last", true);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			}
			try {
				object.put("code", 100);
				object.put("problems", array);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}

		return object;
	}

	/**
	 * 取得指定问题的全部信息
	 * 
	 * @param pid
	 * @return
	 */
	public JSONObject pidQuizAll(int pid, int page) {
		JSONObject object = new JSONObject();
		conn = getConn();
		try {
			ps = conn.prepareStatement("SELECT *FROM t_problem WHERE p_id=?");
			ps.setInt(1, pid);
			rs = ps.executeQuery();
			if (rs.next()) {
				int tid = rs.getInt(3);
				String problem = rs.getString(4);
				String explain = rs.getString(5);

				try {
					object.put("tid", tid);
					ps = conn
							.prepareStatement("SELECT t_topic FROM t_topic WHERE t_id=?  ORDER BY t_date DESC");
					ps.setInt(1, tid);
					rs = ps.executeQuery();
					rs.next();
					object.put("topic", decode(rs.getString(1)));
					object.put("problem", decode(problem));
					object.put("explain", decode(explain));

				} catch (JSONException e) {
					e.printStackTrace();
				}

				ps = conn.prepareStatement("SELECT *FROM t_reply WHERE p_id=?");
				ps.setInt(1, pid);
				rs = ps.executeQuery();
				for (int i = 0; i < (page - 1) * count; i++) {
					if (!rs.next()) {
						return error("长度超出了");
					}
				}
				JSONArray array = new JSONArray();
				for (int i = 0; i < count; i++) {
					if (rs.next()) {
						JSONObject object2 = new JSONObject();
						int rid = rs.getInt(1);
						int uid = rs.getInt(3);
						String reply = rs.getString(4);

						// 名字、性别
						ps = conn
								.prepareStatement("SELECT u_name,u_sex FROM t_user WHERE u_id=?");
						ps.setInt(1, uid);
						ResultSet rs2 = ps.executeQuery();
						rs2.next();
						String name = rs2.getString(1);
						String sex = rs2.getString(2);
						// 赞
						ps = conn
								.prepareStatement("SELECT COUNT(*) FROM t_praise WHERE r_id=?");
						ps.setInt(1, rid);
						rs2 = ps.executeQuery();
						rs2.next();
						int praise = rs2.getInt(1);

						try {
							object2.put("praise", praise);
							object2.put("rid", rid);
							object2.put("uid", uid);
							object2.put("name", decode(name));
							object2.put("sex", decode(sex));
							object2.put("reply", decode(reply));
							object.put("last", false);
							array.put(i, object2);
							close(rs2, null, null);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					} else {
						try {
							object.put("last", true);
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
					}
				}
				try {
					object.put("code", 100);
					object.put("replys", array);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}

		return object;
	}

	/**
	 * 模糊搜索问题（提问）
	 * 
	 * @param problem
	 * @param page
	 * @return
	 */
	public JSONObject likeQuiz(String problem, int page) {
		JSONObject object = new JSONObject();
		conn = getConn();
		try {
			ps = conn
					.prepareStatement("SELECT p_problem,p_id FROM t_problem WHERE p_problem LIKE ?");
			ps.setString(1, "%" + problem + "%");
			rs = ps.executeQuery();
			for (int i = 0; i < (page - 1) * 20; i++) {
				if (!rs.next()) {
					return error("长度已超出");
				}
			}
			JSONArray array = new JSONArray();
			for (int i = 0; i < 20; i++) {

				if (rs.next()) {
					JSONObject object2 = new JSONObject();
					String problem1 = rs.getString(1);
					int pid = rs.getInt(2);
					try {
						object2.put("problem", decode(problem1));
						object2.put("pid", pid);
						object.put("last", false);
						array.put(i, object2);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					try {
						object.put("last", true);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			}
			try {
				object.put("code", 100);
				object.put("problems", array);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}

		return object;
	}

	/**
	 * 模糊搜索话题
	 * 
	 * @param topic
	 * @param page
	 * @return
	 */
	public JSONObject likeTopic(String topic, int page) {
		conn = getConn();
		JSONObject object = new JSONObject();
		try {
			ps = conn
					.prepareStatement("SELECT *FROM t_topic WHERE t_topic LIKE ?");
			ps.setString(1, "%" + topic + "%");
			rs = ps.executeQuery();
			for (int i = 0; i < (page - 1) * 20; i++) {
				if (!rs.next()) {
					return error("长度已超出");
				}
			}
			JSONArray array = new JSONArray();
			for (int i = 0; i < 20; i++) {
				if (rs.next()) {
					JSONObject object2 = new JSONObject();
					String topic1 = rs.getString(2);
					int tid = rs.getInt(1);
					try {
						object2.put("topic", decode(topic1));
						object2.put("tid", tid);
						object.put("last", false);
						array.put(i, object2);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					try {
						object.put("last", true);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			}
			try {
				object.put("code", 100);
				object.put("topics", array);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, ps, conn);
		}

		return object;
	}

}
