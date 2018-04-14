package cn.lyh.problem.test;

import cn.lyh.problem.db.DB;

public class UpdataQuizTest {

	public static void main(String[] args) {
		int pid = 6;
		DB db = DB.getDB();
		ToolsTest tools = new ToolsTest();
		String topic = tools.encode("设计");
		String explain = tools.encode("补充内容");
		System.out.println(db.updataQuiz(pid, topic, explain));
	}

}
