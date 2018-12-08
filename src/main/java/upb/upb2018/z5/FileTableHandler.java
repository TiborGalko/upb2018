/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z5;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import upb.upb2018.z4.*;
import com.google.gson.Gson;
import java.util.List;
import upb.upb2018.z4.Database.Result;

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getParameter("fileName");
        String deleteFileName = request.getParameter("deleteFile");
        System.out.println("Filename " + fileName);
        System.out.println("DecFilename " + deleteFileName);
        if (fileName != null) {            
            Database db = new Database();
            //TODO zranitelnost
            List<String> list = db.getAllCommentsByFileName(fileName);
            if (list.size() > 0) {
                String json = new Gson().toJson(list);

                response.setContentType("text/plain");
                response.getWriter().write(json);
            }
        } else if (deleteFileName != null) {
            Database db = new Database();
            Result r = db.deleteFile(deleteFileName);
            if (r.isResult()) {
                HttpSession session = request.getSession(false);
                String login = (String) session.getAttribute("login");
                List<String> list = db.getAllfiles(login);
                System.out.println("New table size after delete" + list.size());
                if (list.size() > 0) {
                    String json = new Gson().toJson(list);
                    response.setContentType("text/plain");
                    response.getWriter().write(json);
                }
            }
        }
    }

}
