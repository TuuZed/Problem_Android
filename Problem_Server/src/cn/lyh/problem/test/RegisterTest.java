package cn.lyh.problem.test;

import cn.lyh.problem.db.DB;

public class RegisterTest {

	public static void main(String[] args) {
		DB db = DB.getDB();
		ToolsTest tools = new ToolsTest();
		String name = tools.encode("战法师");
		String email = tools.encode("12345@qq.com");
		String passwd = tools.encode("12345678");
		System.out.println(db.register(name, email, passwd));
		
	}

}
