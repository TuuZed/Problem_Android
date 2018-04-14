package cn.lyh.problem.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.lyh.problem.db.DB;

@WebServlet("/updataquiz")
public class UpdataQuizServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public UpdataQuizServlet() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();	
		DB db = DB.getDB();
		String explain = request.getParameter("explain");
		String topic =  request.getParameter("topic");
		int pid	= Integer.parseInt(request.getParameter("pid"));
		out.print(db.updataQuiz(pid, topic, explain));
		
	}

}
