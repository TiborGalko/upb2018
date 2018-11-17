//////////////////////////////////////////////////////////////////////////
// TODO:                                                                //
// Uloha2: Upravte funkciu na prihlasovanie tak, aby porovnavala        //
//         heslo ulozene v databaze s heslom od uzivatela po            //
//         potrebnych upravach.                                         //
// Uloha3: Vlozte do prihlasovania nejaku formu oneskorenia.            //
//////////////////////////////////////////////////////////////////////////
package upb.upb2018.z4;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import upb.upb2018.z4.Database.Result;

public class Login extends HttpServlet {

    private static int delay = 1000; // zvysujuci sa delay po kazdej chybe

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String login = request.getParameter("login");
        String password = request.getParameter("password");

        try {
            Result result = prihlasovanie(login, password);
            System.out.println(result.getMesssage());
            if (result.isResult()) {
                request.getRequestDispatcher("/encrypt.jsp").forward(request, response);
                request.setAttribute("message", result.getMesssage());
            } else {
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                request.setAttribute("message", result.getMesssage());
            }
        } catch (Exception ex) {
            System.err.println("Pri prihlasovani nastala chyba " + ex.getLocalizedMessage());
        }
    }

    /**
     *
     * @param meno
     * @param heslo
     * @return
     * @throws java.security.NoSuchAlgorithmException
     */
    public Result prihlasovanie(String meno, String heslo) throws NoSuchAlgorithmException {
        Database db = new Database();
        Osoba user = db.get(meno);
        if (user == null) {
            return new Result(false, "Uzivatel neexistuje");
        }
        long salt = Security.getSalt(Long.MIN_VALUE, Long.MAX_VALUE);
        String hashedSaltedPass = Security.mixPasswordAndSaltAndHash(heslo, salt);
        if (Security.compareTwoStrings(hashedSaltedPass, user.getPassword())) {
            delay = 1000;
            return new Result(true, "Prihlasenie uspesne");
        } else {
            Security.delay(delay); // delay pri chybe
            delay += 1000;
            return new Result(false, "Chybne heslo");
        }
    }
}
