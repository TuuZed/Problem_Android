package cn.lyh.problem.test;

import cn.lyh.problem.db.DB;

public class TidQuizAtTest {

	public static void main(String[] args) {
		DB db = DB.getDB();
		int tid = 8;
		int page = 1;
		System.out.println(db.tidQuizAt(tid, page));

	}

}
