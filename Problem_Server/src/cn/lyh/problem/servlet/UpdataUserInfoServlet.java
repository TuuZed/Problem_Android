package cn.lyh.problem.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.lyh.problem.db.DB;

@WebServlet("/updatauserinfo")
public class UpdataUserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public UpdataUserInfoServlet() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();	
		DB db = DB.getDB();
		String sex = request.getParameter("sex");
		String intro =  request.getParameter("intro");
		String name =  request.getParameter("name");
		int uid	= Integer.parseInt(request.getParameter("uid"));
		out.print(db.updataUserInfo(uid, name, sex, intro));
		
	}

}
