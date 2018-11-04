/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z3;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
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
public class FileUploadHandler extends HttpServlet {
    
    private final String UPLOAD_DIRECTORY = "C:\\Users\\Jurko\\Documents\\skola\\UPB\\upb2018\\files";
    
    private enum Mode {
        ENCRYPT,
        DECRYPT
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      
        //process only if its multipart content       
        if(ServletFileUpload.isMultipartContent(request)){
            String filename = "";
            try {
                List<FileItem> multiparts = new ServletFileUpload(
                                         new DiskFileItemFactory()).parseRequest(request);
                
                // mode recognition
                Mode mode = multiparts.get(0).getFieldName().contains("enc") ? Mode.ENCRYPT : Mode.DECRYPT;
                
                //variables init
                File temp = null; //inFile
                File encrypted = null;
                File decrypted = null; //outFile
                
                String rsaPK = null;
                
                if(mode == Mode.ENCRYPT) {
                    for(FileItem item : multiparts) {
                        if(!item.isFormField()) {
                            String name = new File(item.getName()).getName();
                            temp = new File(UPLOAD_DIRECTORY + File.separator + name);
                            item.write(temp); // zapisanie suboru do docasneho suboru
                            //encrypted = new File(UPLOAD_DIRECTORY + File.separator + name.substring(0, name.length() - 4) + ".enc");
                            encrypted = new File(UPLOAD_DIRECTORY + File.separator + name + ".enc");
                            //System.out.println("Enc encrypted " + encrypted.getName() + " temp " + temp.getName());
                            filename = encrypted.getName();
                        } else {
                            if(item.getFieldName().equals("enc-rsa-pk")) {
                                rsaPK = item.getString(); //nacitanie rsa public kluca z textfieldu stranky
                            }                          
                        }
                    }                       
                    CryptoUtils.encryptAES(rsaPK, temp, encrypted);
                    deletePlainFile(temp);
                } else {
                    for(FileItem item : multiparts) {
                        if(!item.isFormField()) {
                            String name = new File(item.getName()).getName();
                            temp = new File(UPLOAD_DIRECTORY + File.separator + name);
                            item.write(temp);
                            decrypted = new File(UPLOAD_DIRECTORY + File.separator + name.substring(0, name.length() - 4));               
                            filename = decrypted.getName();
                        } else {
                            if(item.getFieldName().equals("dec-rsa-pk")) {
                                rsaPK = item.getString();
                            }                        
                        }
                    }
                    CryptoUtils.decryptAES(rsaPK, temp, decrypted);
                    deletePlainFile(temp);
                }           
           
               //File uploaded successfully
               request.setAttribute("message", "File Uploaded Successfully");               
            } catch (Exception ex) {
               request.setAttribute("message", "File Enc/Dec Failed due to " + ex);
            }          
            File file = new File(UPLOAD_DIRECTORY, filename);
            response.setHeader("Content-Type", getServletContext().getMimeType(filename));
            response.setHeader("Content-Length", String.valueOf(file.length()));
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            Files.copy(file.toPath(), response.getOutputStream());
         
        }else{
            request.setAttribute("message",
                                 "Sorry this Servlet only handles file upload request");
        }        
    
        request.getRequestDispatcher("/result.jsp").forward(request, response);    
    }
    
    /**
     * Vymazanie docasneho suboru
     * @param file 
     */
    private void deletePlainFile(File file){
        try{
    		if(file.delete()){
    			System.out.println(file.getName() + " is deleted!");
    		}else{
    			System.out.println("Delete operation is failed.");
    		}
    	   
    	}catch(Exception e){    		
    		e.printStackTrace();    		
    	}
    }
}