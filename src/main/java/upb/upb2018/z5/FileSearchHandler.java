/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z5;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import upb.upb2018.z4.Database;

/**
 *
 * @author karol
 */
@WebServlet(name = "FileSearchHandler", urlPatterns = {"/search"})
public class FileSearchHandler extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String login = (String) session.getAttribute("login");
        String searchvyraz = request.getParameter("search");
        if (searchvyraz != null) {
            Database db = new Database();
            List<String> list = db.getSearch(login, searchvyraz);
            String json = new Gson().toJson(list);
            response.setContentType("text/plain");
            response.getWriter().write(json);
        }
    }
}
