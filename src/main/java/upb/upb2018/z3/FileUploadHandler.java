/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z3;

import java.io.File;
import java.io.IOException;
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
 *
 * @author h
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
            try {
                List<FileItem> multiparts = new ServletFileUpload(
                                         new DiskFileItemFactory()).parseRequest(request);
                
                // mode recognition
                Mode mode = multiparts.get(0).getFieldName().contains("enc") ? Mode.ENCRYPT : Mode.DECRYPT;
                
                //variables init
                File temp = null;
                File encrypted = null;
                File decrypted = null;
                File encryptedAESkey = null;
                
                String rsaPK = null;
                String aes = null;
              
                if(mode == Mode.ENCRYPT) {
                    for(FileItem item : multiparts) {
                        if(!item.isFormField()) {
                            String name = new File(item.getName()).getName();
                            temp = new File(UPLOAD_DIRECTORY + File.separator + name);
                            item.write(temp);
                            encrypted = new File(UPLOAD_DIRECTORY + File.separator + name + ".enc");
                            encryptedAESkey = new File(UPLOAD_DIRECTORY + File.separator + "encryptedAESkey.txt");
                        } else {
                            if(item.getFieldName().equals("enc-rsa-pk")) {
                                rsaPK = item.getString();
                            }
                            if(item.getFieldName().equals("enc-aes")) {
                                aes = item.getString();
                            }                             
                        }
                    }
                    CryptoUtils.encryptAES(aes, temp, encrypted);
                    deletePlainFile(temp);
                    CryptoUtils.encryptRSA(rsaPK, aes, encryptedAESkey);
                } else {
                    for(FileItem item : multiparts) {
                        if(!item.isFormField()) {
                            String name = new File(item.getName()).getName();
                            temp = new File(UPLOAD_DIRECTORY + File.separator + name);
                            item.write(temp);
                            decrypted = new File(UPLOAD_DIRECTORY + File.separator + name.substring(0, name.length() - 4));
                        } else {
                            if(item.getFieldName().equals("dec-rsa-pk")) {
                                rsaPK = item.getString();
                            }
                            if(item.getFieldName().equals("dec-aes")) {
                                aes = item.getString();
                            }                             
                        }
                    }
                    //CryptoUtils.decryptRSA(rsaPK, aes, encryptedAESkey);
                    CryptoUtils.decryptAES(aes, temp, decrypted);
                    deletePlainFile(temp);
                }           
           
               //File uploaded successfully
               request.setAttribute("message", "File Uploaded Successfully");
            } catch (Exception ex) {
               request.setAttribute("message", "File Enc/Dec Failed due to " + ex);
            }          
         
        }else{
            request.setAttribute("message",
                                 "Sorry this Servlet only handles file upload request");
        }
    
        request.getRequestDispatcher("/result.jsp").forward(request, response);    
    }
    
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