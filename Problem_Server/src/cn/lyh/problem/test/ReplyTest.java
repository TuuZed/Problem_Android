package cn.lyh.problem.test;

import cn.lyh.problem.db.DB;

public class ReplyTest {

	public static void main(String[] args) {
		int uid = 30;
		int pid =7;
		DB db = DB.getDB();
		ToolsTest tools = new ToolsTest();
		String reply = tools.encode("Ծ‸Ծ因为插座不能每个插口都插上插头 所以需要买更多的排插来用(๑╹∀╹๑)看起来很蠢逼但是提高了销量也算吧w");
		System.out.println(db.reply(uid, pid,reply ));

	}

}
