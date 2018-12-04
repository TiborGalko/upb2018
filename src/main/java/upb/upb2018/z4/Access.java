//////////////////////////////////////////////////////////////////////////
// TODO:                                                                //
// Uloha2: Upravte funkciu na prihlasovanie tak, aby porovnavala        //
//         heslo ulozene v databaze s heslom od uzivatela po            //
//         potrebnych upravach.                                         //
// Uloha3: Vlozte do prihlasovania nejaku formu oneskorenia.            //
//////////////////////////////////////////////////////////////////////////
package upb.upb2018.z4;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Access extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if(request.getSession(false) != null){
            if(request.getRequestURL().toString().contains("encrypt")){
                request.getRequestDispatcher("/encrypt.jsp").forward(request, response);
            } else if(request.getRequestURL().toString().contains("decrypt")){
                request.getRequestDispatcher("/decrypt.jsp").forward(request, response);
            } else if(request.getRequestURL().toString().contains("submit")){
                request.getRequestDispatcher("/submit.jsp").forward(request, response);
            }  else if(request.getRequestURL().toString().contains("share")){
                request.getRequestDispatcher("/share.jsp").forward(request, response);
            }                               
        } else {
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }            
    }

}
