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
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

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
    
    private final String UPLOAD_DIRECTORY = "/usr/local/upb2018";
    
    private enum Mode {
        ENCRYPT,
        DECRYPT
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      
        System.out.println(request.getParameter("sel1"));
        
        //process only if its multipart content       
        if(ServletFileUpload.isMultipartContent(request)){
            try {
                List<FileItem> multiparts = new ServletFileUpload(
                                         new DiskFileItemFactory()).parseRequest(request);                
                
             System.out.println("File submit handler");
             String name = "";
             String to = "";
             for(FileItem item : multiparts) {
                if(!item.isFormField()) {
                    name = new File(item.getName()).getName();
                } else {
                    if(item.getFieldName().equals("file-name")){
                        to = item.getString();
                    }                        
                }
            }                     
                
            System.out.println(name);
            
            } catch (Exception ex) {
            }          
         
        }else{
            request.setAttribute("message",
                                 "Sorry this Servlet only handles file upload request");
        }           
    }
}