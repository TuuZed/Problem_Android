package cn.lyh.problem.test;

import cn.lyh.problem.db.DB;

public class UpdataUserInfo {

	public static void main(String[] args) {
		DB db = DB.getDB();
		ToolsTest tools = new ToolsTest();
		String name = tools.encode("张");
		String sex = tools.encode("女");
		String intro = tools.encode("12345678");
		int uid = 31;
		
		System.out.println(db.updataUserInfo(uid, name, sex, intro));

	}

}
