//////////////////////////////////////////////////////////////////////////
// TODO:                                                                //
// Uloha1: Do suboru s heslami ulozit aj sal.                           //
// Uloha2: Pouzit vytvorenu funkciu na hashovanie a ulozit heslo        //
//         v zahashovanom tvare.                                        //
//////////////////////////////////////////////////////////////////////////
package upb.upb2018.z4;

import java.security.NoSuchAlgorithmException;
import upb.upb2018.z4.Database.MyResult;
import upb.upb2018.z4.Security;


public class Registration {
    protected static MyResult registracia(String meno, String heslo) throws NoSuchAlgorithmException, Exception{
        if (Database.exist("hesla.txt", meno)){
            System.out.println("Meno je uz zabrate.");
            return new MyResult(false, "Meno je uz zabrate.");
        }
        else {
            /*
            *   Salt sa obvykle uklada ako tretia polozka v tvare [meno]:[heslo]:[salt].
            */
             if (!Security.checkParamsOfPassword(heslo)) {
                return new MyResult(false, "Heslo nesplna bezpecnostnu politiku - nedostatocna komplexita");
            } else if (!Security.checkDict(heslo)) {
                return new MyResult(false, "Heslo nesplna bezpecnostnu politiku - heslo je slovnikove.");
            } else {
                //pridame do hesla.txt
                long salt = Security.getSalt(Long.MIN_VALUE, Long.MAX_VALUE);
                Database.add("hesla.txt", meno + ":" + Security.saltedPasswordHashed(heslo, salt) + ":" + salt);
            }
        }
        return new MyResult(true, "");
    }
    
}
