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

    private final String UPLOAD_DIRECTORY = "/home/h/files/";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        //ziskanie login zo session
        String login = (String) session.getAttribute("login");

        //process only if its multipart content       
        if (ServletFileUpload.isMultipartContent(request)) {
            try {
                List<FileItem> multiparts = new ServletFileUpload(
                        new DiskFileItemFactory()).parseRequest(request);

                String prijemcaName = null;
                String fileName = null;
                FileItem writer = null;

                for (FileItem item : multiparts) {
                    if (!item.isFormField()) {
                        fileName = item.getName();
                        writer = item;
                    } else if (item.getFieldName().equals("share-with")) {
                        //share-with je meno uzivatela ktoremu sa subor zdiela 
                        prijemcaName = item.getString();
                    }
                }

                Database db = new Database();
                //ci vsetky testy presli a sprava ktora sa vypise v pripade chyby
                Result result;

                // pozrie sa ci existuje autor a user
                Osoba autor = db.get(login);
                Osoba prijemca = db.get(prijemcaName);
                result = checkExistence(autor, prijemca);

                // generovanie filename ktory neni v databaze
                fileName = checkFileNameInDB(db, fileName);

                File file;
                System.out.println(fileName);
                if (result.isResult()) {
                    // Ulozenie suboru do databazy
                    result = db.saveFileToDB(autor, fileName, prijemca);

                    // ulozenie suboru na disk
                    file = new File(UPLOAD_DIRECTORY + File.separator + login + File.separator + fileName);
                    if(!file.getParentFile().exists()) {
                        if(!file.getParentFile().mkdirs()) {
                            result.setMesssage("Nepodarilo sa ulozit subor na disk");
                            result.setResult(false);
                        }
                    }
                    writer.write(file); // nebude null pretoze file je required pri submite
                }

                request.setAttribute("message", result.getMesssage());
                request.getRequestDispatcher("/share.jsp").forward(request, response);

            } catch (Exception ex) {
                System.out.println(ex);
            }

        } else {
            request.setAttribute("message",
                    "Sorry this Servlet only handles file upload request");
        }
    }

    private Result checkExistence(Osoba autor, Osoba prijemca) {
        if (autor == null) {
            return new Result(false, "Nespravny autor");
        } else if (prijemca == null) {
            return new Result(false, "Prijemca neexistuje");
        }
        return new Result(true, "Check uspesny");
    }

    private String checkFileNameInDB(Database db, String fileName) {
        String[] tokens = fileName.split("\\."); // rozdeli cez bodky
        String newName = tokens[0]; // zobere nazov
        StringBuilder builder = new StringBuilder();
        
        int i = 1;
        while (db.getFile(newName) != null) {
            if (i > 1) {
                newName = newName.substring(0, newName.length() - 4); // odstrani cislovanie ak bolo pridane
            }
            newName += " (" + i + ")";
            tokens[0] = newName;
            
            for (String s : tokens) {
                builder.append(s); // vytvori novy string
            }
            newName = builder.toString(); 
            i++;
        }
        return newName;
    }
}
