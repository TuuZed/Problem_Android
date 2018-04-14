package cn.lyh.problem.test;

import cn.lyh.problem.db.DB;

public class AllQuizTest {

	public static void main(String[] args) {
		DB db = DB.getDB();
		int page = 1;
		System.out.println(db.allQuiz(page));

	}

}
