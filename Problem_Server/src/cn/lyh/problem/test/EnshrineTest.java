package cn.lyh.problem.test;

import cn.lyh.problem.db.DB;

public class EnshrineTest {

	public static void main(String[] args) {
		int uid = 31;
		int rid = 9;
		DB db = DB.getDB();
		System.out.println(db.enshrine(uid, rid));

	}

}
