//////////////////////////////////////////////////////////////////////////
// TODO:                                                                //
// Uloha1: Do suboru s heslami ulozit aj sal.                           //
// Uloha2: Pouzit vytvorenu funkciu na hashovanie a ulozit heslo        //
//         v zahashovanom tvare.                                        //
//////////////////////////////////////////////////////////////////////////
package upb.upb2018.z4;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import upb.upb2018.z4.Database.Result;

public class Registration extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        
        System.out.println(login + " " + password );
        
        try {
            Result result = registracia(login, password);
            System.out.println(result.getMesssage());
            if (result.isResult()) {
                //vytvorenie session
                request.getSession(true);
                response.sendRedirect("encrypt");
                request.setAttribute("message", result.getMesssage());
            } else {
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                request.setAttribute("message", result.getMesssage());
            }
        } catch (IOException | NoSuchAlgorithmException | ServletException ex) {
            System.err.println("Pri registracii nastala chyba " + ex.getLocalizedMessage());
        }
    }

    protected static Result registracia(String meno, String heslo) throws NoSuchAlgorithmException, IOException {
        if (!Security.checkParamsOfPassword(heslo)) {
            return new Result(false, "Heslo nesplna bezpecnostnu politiku - nedostatocna komplexita");
        } else if (!Security.checkDict(heslo)) {
            return new Result(false, "Heslo nesplna bezpecnostnu politiku - heslo je slovnikove.");
        }

        Database db = new Database();
        long salt = Security.getSalt(Long.MIN_VALUE, 50);
        String hashedSaltedPass = Security.mixPasswordAndSaltAndHash(heslo, salt);
        Osoba user = new Osoba(meno, hashedSaltedPass, salt);
        Result r = db.add(user);
        if(r.isResult()) {
            return new Result(true, "Uzivatel uspesne vytvoreny");            
        } else {
            return new Result(false, "Uzivatela sa nepodarilo vytvorit " + r.getMesssage()); 
        }        
    }

}
