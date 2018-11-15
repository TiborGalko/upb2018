//////////////////////////////////////////////////////////////////////////
// TODO:                                                                //
// Uloha2: Upravte funkciu na prihlasovanie tak, aby porovnavala        //
//         heslo ulozene v databaze s heslom od uzivatela po            //
//         potrebnych upravach.                                         //
// Uloha3: Vlozte do prihlasovania nejaku formu oneskorenia.            //
//////////////////////////////////////////////////////////////////////////
package upb.upb2018.z4;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.StringTokenizer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import upb.upb2018.z3.CryptoUtils;
import upb.upb2018.z3.FileUploadHandler;
import upb.upb2018.z4.Database.MyResult;
import upb.upb2018.z4.Security;

public class Login extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         System.out.println("Login.java");
              if(ServletFileUpload.isMultipartContent(request)){
            try {
                List<FileItem> multiparts = new ServletFileUpload(
                                         new DiskFileItemFactory()).parseRequest(request);
                
         
                String login = "";
                String pass = "";
                MyResult result;
                String sprava;
                
   
                    for(FileItem item : multiparts) {
                            if(item.getFieldName().equals("login")) {
                                login = item.getString(); //nacitanie rsa public kluca z textfieldu stranky
                                System.out.println(login);
                            }  
                               if(item.getFieldName().equals("password")) {
                                pass = item.getString(); //nacitanie rsa public kluca z textfieldu stranky
                                 System.out.println(pass);
                            }
                    }     
                    
                     result = prihlasovanie(login,pass);
                     System.out.println(result);
                     System.out.println(result.getFirst());
                     System.out.println(result.getSecond());
                    if(result.getFirst()){
                    request.getRequestDispatcher("/encrypt.jsp").forward(request, response);
                    }else{
                        request.getRequestDispatcher("/encrypt.jsp").forward(request, response);
                     sprava = result.getSecond();
                     request.setAttribute("message", sprava );
                    }

                             
            } catch (Exception ex) {
               request.setAttribute("message", "File Enc/Dec Failed due to " + ex);
            }          
                     
        }else{
            request.setAttribute("message",
                                 "Sorry this Servlet only handles file upload request");
        }
              
        
    }
    
    
    public static MyResult prihlasovanie(String meno, String heslo) throws IOException, Exception{
        try{ 
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("upb2018PU");
        EntityManager em = emf.createEntityManager();
           TypedQuery<Osoba> q = em.createNamedQuery("Osoba.findAll", Osoba.class);
        
        for (Osoba o: q.getResultList()) {
           //System.out.println(o.getId() + " : " + o.getMeno() + " -> " + o.getVyska() + ", " + o.getVaha());
           System.out.println(o);
        }}catch(Exception e){System.out.println("je to v pici" + e);}
        Security.delay(1000);
        MyResult account = Database.find("hesla.txt", meno);
        if (!account.getFirst()){
            return new MyResult(false, "Nespravne meno.");
        }
        else {
            StringTokenizer st = new StringTokenizer(account.getSecond(), ":");
            st.nextToken();      //prvy token je prihlasovacie meno
            /*
            *   Pred porovanim hesiel je nutne k heslu zadanemu od uzivatela pridat prislusny salt z databazy a nasledne tento retazec zahashovat.
            */
            boolean rightPassword = st.nextToken().equals(heslo);
            if (!rightPassword)    
                return new MyResult(false, "Nespravne heslo.");
        }
        return new MyResult(true, "Uspesne prihlasenie.");
    }
}
