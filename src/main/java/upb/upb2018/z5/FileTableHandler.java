/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z5;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import upb.upb2018.z4.*;
import com.google.gson.Gson;
import java.util.List;

/**
 *
 * @author karol
 */
public class FileTableHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        HttpSession session = request.getSession(false);
        String login = (String)session.getAttribute("login");
        
        Database db = new Database();
        List<String> list = db.getAllfiles(login);
        String json = new Gson().toJson(list);
        
        response.setContentType("text/plain");
	response.getWriter().write(json);
    }

}
