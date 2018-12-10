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
import java.io.File;
import java.util.List;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;
import upb.upb2018.z4.Database.Result;

/**
 *
 * @author karol
 */
public class FileTableHandler extends HttpServlet {

    private final String UPLOAD_DIRECTORY = "/usr/local/upb2018";

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
        String fileName = escapeHtml(request.getParameter("fileName"));
        String deleteFileName = escapeHtml(request.getParameter("deleteFile"));

        if (fileName != null && !"".equals(fileName)) {
            Database db = new Database();
            
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

                // Vymazanie na disku
                File s = new File(UPLOAD_DIRECTORY + File.separator + login + File.separator + deleteFileName + ".enc");
                System.out.println(s.getAbsolutePath());
                try {
                    if (s.delete()) {
                        System.out.println(s.getName() + " is deleted!");
                    } else {
                        System.out.println("Delete operation is failed.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                List<String> list = db.getAllfiles(login);
                if (list.size() > 0) {
                    String json = new Gson().toJson(list);
                    response.setContentType("text/plain");
                    response.getWriter().write(json);
                }
            }
        }
    }

}
