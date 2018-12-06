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
        if (!request.getParameter("fileName").isEmpty() && !request.getParameter("share-with").isEmpty()) {
            share(login, request.getParameter("fileName"), request.getParameter("share-with"));
        } else if (!request.getParameter("fileNameComment").isEmpty() && !request.getParameter("comment").isEmpty()) {
            addComment(login, request.getParameter("comment"), request.getParameter("fileNameComment"));
        }
    }

    private Result addComment(String login, String komentar, String fileName) {
        Result result = new Result(false, "Chyba");
        try {
            Database db = new Database();
            Osoba autor = db.get(login);
            result = db.addComment(fileName, komentar, autor); 
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result;
    }

    /**
     * Metoda prida prijemcu v databaze k suboru podla filename parametra
     *
     * @param login username autora
     * @param fileName nazov suboru
     * @param prijemcaName username prijemcu
     * @return Result objekt s hodnotou true false a spravou
     */
    private Result share(String login, String fileName, String prijemcaName) {
        Result result = new Result(false, "Chyba");
        try {
            Database db = new Database();
            Osoba prijemca = db.get(prijemcaName);
            Osoba autor = db.get(login);
            result = checkExistence(autor, prijemca);
            if (result.isResult() == false) {
                return result;
            }
            String newFileName = db.checkFileName(fileName);
            result = db.addPrijemcaToFile(newFileName, prijemca);
            if (result.isResult() == false) {
                return result;
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result;
    }

    /**
     * Metoda pozrie ci autor a prijemca existuju
     *
     * @param autor username autora
     * @param prijemca username prijemcu
     * @return Result object s true/false a spravou
     */
    private Result checkExistence(Osoba autor, Osoba prijemca) {
        if (autor == null) {
            return new Result(false, "Nespravny autor");
        } else if (prijemca == null) {
            return new Result(false, "Prijemca neexistuje");
        }
        return new Result(true, "Check uspesny");
    }
}
