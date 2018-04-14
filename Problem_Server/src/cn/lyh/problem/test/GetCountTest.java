package cn.lyh.problem.test;

import cn.lyh.problem.db.DB;

public class GetCountTest {
	public static void main(String[] args) {
		int uid = 30;
		DB db = DB.getDB();
		System.out.println(db.getCount(uid));
	}
}
