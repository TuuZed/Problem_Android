package cn.lyh.problem.test;

import cn.lyh.problem.db.DB;

public class UidReplyTest {

	public static void main(String[] args) {
		int uid =30;
		int page = 1;
		DB db = DB.getDB();
		System.out.println(db.uidReply(uid, page));

	}

}
