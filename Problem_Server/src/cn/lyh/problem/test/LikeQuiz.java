package cn.lyh.problem.test;

import cn.lyh.problem.db.DB;

public class LikeQuiz {

	public static void main(String[] args) {
		DB db = DB.getDB();
		ToolsTest tools = new ToolsTest();
		String problem = tools.encode("设");
		int page = 1;
		
		System.out.println(db.likeQuiz(problem, page));

	}

}
