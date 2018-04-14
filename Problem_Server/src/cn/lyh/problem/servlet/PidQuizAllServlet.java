package cn.lyh.problem.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.lyh.problem.db.DB;

@WebServlet("/pidquizall")
public class PidQuizAllServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public PidQuizAllServlet() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();	
		DB db = DB.getDB();
		int page	= Integer.parseInt(request.getParameter("page"));
		int pid	= Integer.parseInt(request.getParameter("pid"));
		out.print(db.pidQuizAll(pid, page));
		
	}

}
