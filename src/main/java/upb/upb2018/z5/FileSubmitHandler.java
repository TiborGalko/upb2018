/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z5;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;
import upb.upb2018.z4.Database;
import upb.upb2018.z4.Database.Result;
import upb.upb2018.z4.Osoba;

/**
 * @author Karasek, Galko, Sokolova, Krist
 */
public class FileSubmitHandler extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        //ziskanie login zo session
        String login = (String) session.getAttribute("login");
        Result result = new Result(false, "Chyba");
                
        String fileNameComment = escapeHtml(request.getParameter("fileNameComment"));
        String comment = escapeHtml(request.getParameter("comment"));
        if (fileNameComment != null && !"".equals(fileNameComment) && comment != null && !"".equals(comment)) {
            result = addComment(login, comment, fileNameComment);
        }
        request.setAttribute("message", result.getMesssage());
        request.getRequestDispatcher("/decrypt.jsp").forward(request, response);
    }

    private Result addComment(String login, String komentar, String fileName) {
        Result result = new Result(false, "Chyba");
        String escaped = escapeHtml(komentar);
        try {
            Database db = new Database();
            Osoba autor = db.get(login);
            result = db.addComment(fileName, escaped, autor);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result;
    }   
}
