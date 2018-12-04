/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z5;

import upb.upb2018.z3.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import upb.upb2018.z4.Osoba;

/**
 * https://asecuritysite.com/encryption/keygen?fbclid=IwAR3R6nwJ2M9L1iX28kXhxpeGnNlzpMNG07twgE1b22YxC9r55u7Vt9TJiZw
 * http://www.csfieldguide.org.nz/en/interactives/rsa-key-generator/index.html?fbclid=IwAR3Fdlci0yQW_5rURW1QhvaNbh2d1pTQ8GBLVTB2rcHD4-IoACcvOWN8F7M
 * https://8gwifi.org/rsafunctions.jsp?fbclid=IwAR2L0j9ajqAfSOdMUODjrJzs3ujqMstFXlN9-2i5dvpWu_lGzsuu-rHBwb4
 * 
 * https://stackoverflow.com/questions/6481627/java-security-illegal-key-size-or-default-parameters
 * 
 * @author Karasek, Galko, Sokolova, Krist
 */
public class FileSubmitHandler extends HttpServlet {
    
    private final String UPLOAD_DIRECTORY = "/home/juraj/Documents/files";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      
        HttpSession session = request.getSession(false);
        String login = (String)session.getAttribute("login");
        
        //process only if its multipart content       
        if(ServletFileUpload.isMultipartContent(request)){
            try {
                List<FileItem> multiparts = new ServletFileUpload(
                                         new DiskFileItemFactory()).parseRequest(request);   
                
                File file = null;
                String prijemcaName = null;
                String fileName = null;
                FileItem writer = null;
                
                for(FileItem item : multiparts) {
                    if(!item.isFormField()) {
                        fileName = new File(item.getName()).getName();
                        writer = item;
                } else {
                    if(item.getFieldName().equals("share-with")){
                        prijemcaName = item.getString();
                    }                        
                }
            }       
                
            upb.upb2018.z4.Database db = new upb.upb2018.z4 .Database();
            
            //ci vsetky testy presli
            boolean test = true;
            //sprava ktora sa vypise v pripade chyby
            String message = null;
            
            //Autor
            Osoba autor = db.get(login); 
            Osoba prijemca = db.get(prijemcaName);
            if(autor == null){
                test = false;
                message = "Nespravny autor";
            }else if(prijemca == null){
                test = false;
                message = "Prijemca neexistuje";
            }
            
            //Subor            
            int i = 1;
            while(db.getFile(fileName) != null){
                if(i > 1){
                     fileName = fileName.substring(0, fileName.length() - 3);
                }
                fileName = fileName + " (" + i + ")";
                i++;
            }
            
            System.out.println(fileName);
            if(test){
                Subor subor = new Subor();
                subor.setAutor(autor);
                subor.setNazov(fileName);
                List<Osoba> list = new ArrayList();
                list.add(prijemca);
                subor.setZdielajuci(list);
            
                db.persist(subor); 
                file = new File(UPLOAD_DIRECTORY + File.separator + login + File.separator + fileName);
                file.getParentFile().mkdirs();
                writer.write(file);
                message = "Subor uspesne pridany";
                request.setAttribute("message", message);
                request.getRequestDispatcher("/share.jsp").forward(request, response);
            } else {
                request.setAttribute("message", message);
                request.getRequestDispatcher("/share.jsp").forward(request, response);
            }
            
            
        } catch (Exception ex) {
            System.out.println(ex);
        }          
         
        }else{
            request.setAttribute("message",
                                 "Sorry this Servlet only handles file upload request");
        }           
    }
}