package cn.lyh.problem.test;

import cn.lyh.problem.db.DB;

public class AllQuizAtTest {

	public static void main(String[] args) {
		DB db = DB.getDB();
		int page = 1;
		System.out.println(db.allQuizAt(page));

	}

}
