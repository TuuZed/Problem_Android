package cn.lyh.problem.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.lyh.problem.db.DB;

@WebServlet("/unenshrine")
public class UnEnshrineServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public UnEnshrineServlet() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();	
		DB db = DB.getDB();
		int rid	= Integer.parseInt(request.getParameter("rid"));
		int uid	= Integer.parseInt(request.getParameter("uid"));
		out.print(db.unEnshrine(uid, rid));
		
	}

}
