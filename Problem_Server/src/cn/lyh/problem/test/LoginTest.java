package cn.lyh.problem.test;

import cn.lyh.problem.db.DB;

public class LoginTest {
	public static void main(String[] args) {
		DB db = DB.getDB();
		ToolsTest tools = new ToolsTest();
		String email = tools.encode("1234@qq.com");
		String passwd = tools.encode("12345678");
		System.out.println(db.login(email, passwd));
	}
}
