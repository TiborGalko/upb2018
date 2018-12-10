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

    private final String UPLOAD_DIRECTORY = "usr/local/upb2018";

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
        Database db = new Database();
        // pozrie sa ci existuje autor a user
        Osoba aktualnyUser = db.get(login);
        //process only if its multipart content
        boolean error = false;
        if (ServletFileUpload.isMultipartContent(request)) {
            String filename = "";
            Mode mode = Mode.ENCRYPT;
            //ci vsetky testy presli a sprava ktora sa vypise v pripade chyby                    
            Result result = new Result(false, "Neocakavana chyba");

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
                String recipient = null;
                String filenameInDb = null;

                if (mode == Mode.ENCRYPT) {

                    for (FileItem item : multiparts) {
                        if (!item.isFormField()) {
                            String name = new File(item.getName()).getName();
                            name = escapeHtml(name);
                            if (name.contains("/") || name.contains("\\")) {
                                result.setMesssage("Name in file enc is not correct");
                                error = true;
                                break;
                            }
                            temp = new File(UPLOAD_DIRECTORY + File.separator + aktualnyUser.getLogin() + File.separator + name + "-temp");
                            Path p = FileSystems.getDefault().getPath(temp.getAbsolutePath());
                            if (!Files.exists(p)) {
                                System.out.println("File not exists " + p);
                                temp.getParentFile().mkdirs();
                            }
                            item.write(temp); // zapisanie suboru do docasneho suboru                            

                            filenameInDb = db.checkFileName(name);
                            encrypted = new File(UPLOAD_DIRECTORY + File.separator + aktualnyUser.getLogin() + File.separator + filenameInDb + ".enc");
                            System.out.println("Enc encrypted " + encrypted.getName() + " temp " + temp.getName());
                            filename = encrypted.getName();

                            System.err.println(result.getMesssage());
                        } else {
                            if (item.getFieldName().equals("enc-recipient")) {
                                recipient = item.getString();
                                Osoba o = db.get(recipient);
                                if (o != null) {
                                    rsaPK = o.getPubKey(); //nacitanie rsa public kluca od usera
                                } else {
                                    result.setMesssage("Recipient neexistuje");
                                    error = true;
                                }
                            }
                        }
                    }
                    if (!error) {
                        System.out.println("encrypting file " + encrypted.getAbsolutePath());
                        CryptoUtils.encryptAES(rsaPK, temp, encrypted);
                        deletePlainFile(temp);
                        // ulozenie zasifrovaneho suboru do databazy
                        result = db.saveFileToDB(aktualnyUser, filenameInDb);
                        if (result.isResult() == false) {
                            error = true;
                        }

                        if (filename != null && !"".equals(filename)) {
                            result = share(login, filenameInDb, recipient);
                            if (result.isResult() == false) {
                                error = true;
                            }
                        }
                    } else {
                        request.setAttribute("message", result.getMesssage());
                        request.getRequestDispatcher("/encrypt.jsp").forward(request, response);
                    }
                } else {
                    for (FileItem item : multiparts) {
                        if (!item.isFormField()) {
                            String name = new File(item.getName()).getName();
                            if (name.contains("/") || name.contains("\\")) {
                                result.setMesssage("Name in file dec is not correct");
                                error = true;
                                break;
                            }
                            temp = new File(UPLOAD_DIRECTORY + File.separator + aktualnyUser.getLogin() + File.separator + name + "-temp");
                            item.write(temp);
                            decrypted = new File(UPLOAD_DIRECTORY + File.separator + aktualnyUser.getLogin() + File.separator + name.substring(0, name.length() - 4));
                            //System.out.println("Enc decrypted " + decrypted.getName() + " temp " + temp.getName());
                            filename = decrypted.getName();
                        }
                    }
                    if (!error) {
                        CryptoUtils.decryptAES(aktualnyUser.getPrivKey(), temp, decrypted);
                        deletePlainFile(temp);
                    } else {
                        request.setAttribute("message", "File Enc/Dec Failed");
                        request.getRequestDispatcher("/decrypt.jsp").forward(request, response);
                    }
                }
                //File uploaded successfully
                request.setAttribute("message", "File Uploaded Successfully");
            } catch (Exception ex) {
                System.err.println("exception " + ex.getLocalizedMessage());
                request.setAttribute("message", "File Enc/Dec Failed due to " + ex);
            }
            if (!error) {
                File file = new File(UPLOAD_DIRECTORY + File.separator + aktualnyUser.getLogin(), filename);

                response.setHeader("Content-Type", getServletContext().getMimeType(filename));
                response.setHeader("Content-Length", String.valueOf(file.length()));
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                Files.copy(file.toPath(), response.getOutputStream());
                if (mode == Mode.DECRYPT) {
                    deletePlainFile(file);
                }
            } else {
                System.err.println("niekde nastala chyba " + result.getMesssage());

            }
        } else {
            String decFilename = escapeHtml(request.getParameter("dec-filename"));
            if (decFilename != null && !"#".equals(decFilename) && !"".equals(decFilename) && !decFilename.contains("//") && !decFilename.contains("\\")) {
                Subor s = db.getFile(decFilename);
                String autorLogin = s.getAutor().getLogin();
                File file = new File(UPLOAD_DIRECTORY + File.separator + autorLogin + File.separator + decFilename + ".enc");
                System.err.println("Downloading file " +file.getAbsolutePath());
                System.err.println("For user " + aktualnyUser);
                if (file.exists() && aktualnyUser != null) {
                    try {
                        File decrypted = new File(UPLOAD_DIRECTORY + File.separator + autorLogin + File.separator + decFilename);
                        Path p = FileSystems.getDefault().getPath(decrypted.getAbsolutePath());
                        // Create the empty file with default permissions, etc.
                        Files.createFile(p);
                        CryptoUtils.decryptAES(aktualnyUser.getPrivKey(), file, decrypted);

                        String decryptedFilename = decrypted.getName();

                        response.setHeader("Content-Type", getServletContext().getMimeType(decryptedFilename));
                        response.setHeader("Content-Length", String.valueOf(decrypted.length()));
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + decryptedFilename + "\"");
                        Files.copy(decrypted.toPath(), response.getOutputStream());
                        deletePlainFile(decrypted);
                    } catch (Exception ex) {
                        request.setAttribute("message", "File Dec File Failed due to " + ex);
                    }
                } else {
                    request.setAttribute("message", "File doesnt exist or user is not in database");
                    request.getRequestDispatcher("/encrypt.jsp").forward(request, response);
                }
            }
        }
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
            //String newFileName = db.checkFileName(fileName);
            result = db.addPrijemcaToFile(fileName, prijemca);
            if (result.isResult() == false) {
                return result;
            }
        } catch (Exception ex) {
            System.err.println(ex);
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

    /**
     * Vymazanie docasneho suboru
     *
     * @param file
     */
    private void deletePlainFile(File file) {
        try {
            if (file.delete()) {
                System.out.println(file.getName() + " is deleted!");
            } else {
                System.out.println("Delete operation is failed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
