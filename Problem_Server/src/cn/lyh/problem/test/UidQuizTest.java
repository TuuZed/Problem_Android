package cn.lyh.problem.test;

import cn.lyh.problem.db.DB;

public class UidQuizTest {

	public static void main(String[] args) {
		int uid =31;
		int page = 1;
		DB db = DB.getDB();
		System.out.println(db.uidQuiz(uid, page));
	}

}
