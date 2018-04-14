package cn.lyh.problem.test;

import cn.lyh.problem.db.DB;

public class UnEnshrineTest {

	public static void main(String[] args) {
		int uid = 31;
		int rid = 7;
		DB db = DB.getDB();
		System.out.println(db.unEnshrine(uid, rid));

	}

}
