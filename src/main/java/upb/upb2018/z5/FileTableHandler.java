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
import com.google.gson.JsonObject;
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
        String login = (String) session.getAttribute("login");

        Database db = new Database();
        List<String> list = db.getAllfiles(login);
        String json = new Gson().toJson(list);

        response.setContentType("text/plain");
        response.getWriter().write(json);
    }

    // TODO volaco je tu strasne dojebane a nechce to chodit treba tu vytiahnut data pre ten subor ako json ale ani za nic
    // ajax vola tuto metodu zo share
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {   
            String file = request.getParameter("filename");
            Database db = new Database();
            Subor s = db.getFile(file);
            String json = "{'nazov': '"+s.getNazov()+"', 'autor': '"+s.getAutor().getLogin()+"'}";                     
            System.out.println(json);
        } catch (Exception ex) {
            // System.err.println("Initial SessionFactory creation failed.");
            ex.printStackTrace();
            System.exit(0);
        }

    }

}
