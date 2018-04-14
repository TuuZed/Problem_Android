package cn.lyh.problem.test;

import cn.lyh.problem.db.DB;

public class QuizTest {

	public static void main(String[] args) {
		int uid = 31;
		DB db = DB.getDB();
		ToolsTest tools = new ToolsTest();
		String problem =  tools.encode("有哪些看似很蠢，其实很实用的设计？ 6");
		String topic =  tools.encode("设计");
		String explain =  tools.encode("");
		System.out.println(db.quiz(uid, problem, topic, explain));

	}

}
