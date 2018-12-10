/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z3;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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
import upb.upb2018.z5.Subor;

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

    private final String UPLOAD_DIRECTORY = "/usr/local/upb2018";

    private enum Mode {
        ENCRYPT,
        DECRYPT
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        //ziskanie login zo session
        String login = (String) session.getAttribute("login");

        //process only if its multipart content       
        if (ServletFileUpload.isMultipartContent(request)) {
            String filename = "";
            Mode mode = Mode.ENCRYPT;
            boolean error = false;

            Database db = new Database();
            // pozrie sa ci existuje autor a user
            Osoba autor = db.get(login);
            // generovanie filename ktory neni v databaze
            try {
                List<FileItem> multiparts = new ServletFileUpload(
                        new DiskFileItemFactory()).parseRequest(request);

                // mode recognition
                mode = multiparts.get(0).getFieldName().contains("enc") ? Mode.ENCRYPT : Mode.DECRYPT;

                //variables init
                File temp = null; //inFile
                File encrypted = null;
                File decrypted = null; //outFile

                String rsaPK = null;

                //ci vsetky testy presli a sprava ktora sa vypise v pripade chyby                    
                Result result = new Result(false, "Neocakavana chyba");

                if (mode == Mode.ENCRYPT) {

                    for (FileItem item : multiparts) {
                        if (!item.isFormField()) {
                            String name = new File(item.getName()).getName();

                            if (name.contains("/") || name.contains("\\")) {
                                error = true;
                                break;
                            }

                            temp = new File(UPLOAD_DIRECTORY + File.separator + autor.getLogin() + File.separator + name + "-temp");
                            Path p = FileSystems.getDefault().getPath(temp.getAbsolutePath());
                            if (!Files.exists(p)) {
                                temp.getParentFile().mkdirs();
                            }
                            item.write(temp); // zapisanie suboru do docasneho suboru                            

                            String newFileName = db.checkFileName(name);
                            encrypted = new File(UPLOAD_DIRECTORY + File.separator + autor.getLogin() + File.separator + newFileName + ".enc");
                            //System.out.println("Enc encrypted " + encrypted.getName() + " temp " + temp.getName());
                            filename = encrypted.getName();

                            // ulozenie zasifrovaneho suboru do databazy
                            result = db.saveFileToDB(autor, newFileName);
                            System.err.println(result.getMesssage());
                        } else {
                            if (item.getFieldName().equals("enc-rsa-pk")) {
                                rsaPK = item.getString(); //nacitanie rsa public kluca z textfieldu stranky                                 
                            }
                        }
                    }
                    if (!error) {
                        CryptoUtils.encryptAES(rsaPK, temp, encrypted);
                        deletePlainFile(temp);
                    } else {
                        request.setAttribute("message", result.getMesssage());
                        request.getRequestDispatcher("/encrypt.jsp").forward(request, response);
                    }
                } else {
                    for (FileItem item : multiparts) {
                        if (!item.isFormField()) {
                            String name = new File(item.getName()).getName();
                            if (name.contains("/") || name.contains("\\")) {
                                error = true;
                                break;
                            }

                            temp = new File(UPLOAD_DIRECTORY + File.separator + autor.getLogin() + File.separator + name + "-temp");
                            item.write(temp);

                            decrypted = new File(UPLOAD_DIRECTORY + File.separator + autor.getLogin() + File.separator + name.substring(0, name.length() - 4));

                            //System.out.println("Enc decrypted " + decrypted.getName() + " temp " + temp.getName());
                            filename = decrypted.getName();
                        } else {
                            if (item.getFieldName().equals("dec-rsa-pk")) {
                                rsaPK = item.getString();
                            }
                        }
                    }
                    if (!error) {
                        CryptoUtils.decryptAES(rsaPK, temp, decrypted);
                        deletePlainFile(temp);
                    } else {
                        request.setAttribute("message", "File Enc/Dec Failed");
                        request.getRequestDispatcher("/decrypt.jsp").forward(request, response);
                    }
                }
                //File uploaded successfully
                request.setAttribute("message", "File Uploaded Successfully");
            } catch (Exception ex) {
                request.setAttribute("message", "File Enc/Dec Failed due to " + ex);
            }
            if (!error) {
                File file = new File(UPLOAD_DIRECTORY + File.separator + autor.getLogin(), filename);
                                
                response.setHeader("Content-Type", getServletContext().getMimeType(filename));
                response.setHeader("Content-Length", String.valueOf(file.length()));
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                Files.copy(file.toPath(), response.getOutputStream());
                if (mode == Mode.DECRYPT) {
                    deletePlainFile(file);
                }
                
            }
        } else {
            String decFilename = escapeHtml(request.getParameter("dec-filename"));
            String rsaPK = request.getParameter("dec-rsa-pk-file");   
            if (decFilename != null && !"#".equals(decFilename) && !"".equals(decFilename)
                    && !decFilename.contains("//") && !decFilename.contains("\\") && rsaPK != null
                    && !"#".equals(rsaPK) && !"".equals(rsaPK)) {                
                Database db = new Database();
                Subor s = db.getFile(decFilename);
                String autorLogin = s.getAutor().getLogin();
                File file = new File(UPLOAD_DIRECTORY + File.separator + autorLogin + File.separator + decFilename);
                if (file.exists()) {
                    try {
                        File decrypted = new File(UPLOAD_DIRECTORY + File.separator + autorLogin + File.separator + decFilename.substring(0, decFilename.length() - 4));
                        Path p = FileSystems.getDefault().getPath(decrypted.getAbsolutePath());
                        // Create the empty file with default permissions, etc.
                        Files.createFile(p);
                        CryptoUtils.decryptAES(rsaPK, file, decrypted);

                        String decryptedFilename = decrypted.getName();

                        response.setHeader("Content-Type", getServletContext().getMimeType(decryptedFilename));
                        response.setHeader("Content-Length", String.valueOf(decrypted.length()));
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + decryptedFilename + "\"");
                        Files.copy(decrypted.toPath(), response.getOutputStream());
                        deletePlainFile(decrypted);
                    } catch (Exception ex) {
                        request.setAttribute("message", "File Dec File Failed due to " + ex);
                    }
                }
            }
        }
    }

    /**
     * Vymazanie docasneho suboru
     *
     * @param file
     */
    private void deletePlainFile(File file) {
        try {
            if (file.delete()) {
                //System.out.println(file.getName() + " is deleted!");
            } else {
                //System.out.println("Delete operation is failed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
