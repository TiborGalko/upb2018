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
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;
import upb.upb2018.z4.Database.Result;

public class Registration extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String login = escapeHtml(request.getParameter("login"));
        String password = escapeHtml(request.getParameter("password"));
        if (login != null && !"".equals(login) && password != null && !"".equals(password)) {
            try {
                Result result = registracia(login, password);               
                if (result.isResult()) {
                    //vytvorenie session
                    //request.getSession(true);
                    request.setAttribute("message", result.getMesssage());
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                } else {
                    request.setAttribute("message", result.getMesssage());
                    request.getRequestDispatcher("/register.jsp").forward(request, response);
                }
            } catch (IOException | NoSuchAlgorithmException | ServletException ex) {
                System.err.println("Pri registracii nastala chyba " + ex.getLocalizedMessage());
            }
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
        if (r.isResult()) {
            return new Result(true, "Uzivatel uspesne vytvoreny");
        } else {
            return new Result(false, "Uzivatela sa nepodarilo vytvorit " + r.getMesssage());
        }
    }

}
