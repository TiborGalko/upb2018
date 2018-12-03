//////////////////////////////////////////////////////////////////////////
// TODO:                                                                //
// Uloha1: Vytvorit funkciu na bezpecne generovanie saltu.              //
// Uloha2: Vytvorit funkciu na hashovanie.                              //
// Je vhodne vytvorit aj dalsie pomocne funkcie napr. na porovnavanie   //
// hesla ulozeneho v databaze so zadanym heslom.                        //
//////////////////////////////////////////////////////////////////////////
package upb.upb2018.z4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.passay.DictionaryRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.dictionary.WordListDictionary;
import org.passay.dictionary.WordLists;
import org.passay.dictionary.sort.ArraysSort;


public class Security {
    
    private static final String DICT_PATH = "/home/juraj/Documents";

    private static String hash(String password) throws NoSuchAlgorithmException{  
        /*
        *   Pred samotnym hashovanim si najskor musite ulozit instanciu hashovacieho algoritmu.
        *   Hash sa uklada ako bitovy retazec, takze ho nasledne treba skonvertovat na String (napr. cez BigInteger);
        */
        MessageDigest messDig = MessageDigest.getInstance( "SHA-256" );
        messDig.update( password.getBytes( StandardCharsets.UTF_8 ) );
        byte[] digest = messDig.digest();
        String hash = bytesToHex(digest);
        return hash;
    }
    
    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    public static Boolean compareTwoStrings(String str1, String str2){
        return str1.equals(str2);
    }
    
    public static void delay(int ms){
        try {
            Thread.sleep(ms);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    
    protected static String mixPasswordAndSalt(String password, long salt) {
        String strSalt = Long.toString(salt);
        String mixedPass = password + strSalt;
        System.out.println("mixedPass = " + mixedPass + " password = " + password + " salt = " + salt);
        return mixedPass;
    }
    
    protected static String mixPasswordAndSaltAndHash(String password, long salt) throws NoSuchAlgorithmException {
        String strSalt = Long.toString(salt);
        String mixedPass = password + strSalt;
        System.out.println("mixedPass = " + mixedPass + " password = " + password + " salt = " + salt);
        return hash(mixedPass);
    }
    
    protected static long getSalt(long min, long max) {
        /*
        *   Salt treba generovat cez secure funkciu.
        */
        long salt = 0;
        Random randomGen = new Random();
        SecureRandom ranGenSalt = new SecureRandom();
        ranGenSalt.setSeed(randomGen.nextInt(100));
        do {
            salt = ranGenSalt.nextLong();
            System.out.println("gen salt = " + salt);
        } while (salt < min || salt > max);
        System.out.println("Random generated salt = " + salt);
        return salt;
    }
    
    protected static String saltedPasswordHashed(String password, long salt) throws NoSuchAlgorithmException{
        return hash(mixPasswordAndSalt(password, salt));
    }
    
    protected static Boolean checkParamsOfPassword(String password){
        int minLength = 8;
        if (!bigAlphaInside(password)){
            return false;
        }
        
        if (!littleAlphaInside(password)){
            return false;
        }
        
        if (!numberInside(password)){
            return false;
        }
        
        if (!weightInside(password, minLength)){
            return false;
        }
        
        return true;
    }
    
    private static Boolean bigAlphaInside(String password){
        password = password.replaceAll("[^A-Z]", "");
        return password.length() > 0; 
    }
    
    private static Boolean littleAlphaInside(String password){
        password = password.replaceAll("[^a-z]", "");
        return password.length() > 0;
    }
    
    private static Boolean numberInside(String password){
        password = password.replaceAll("[^0-9]", "");
        return password.length() > 0;
    }
    
    private static Boolean weightInside(String password, int minLenght){
        return minLenght != password.length();
    }
    
    protected static Boolean checkDict(String password) throws FileNotFoundException, IOException {
        DictionaryRule rule = new DictionaryRule(
            new WordListDictionary(WordLists.createFromReader(
            // Reader around the word list file
            //new FileReader[] {new FileReader("10k_most_common.txt")},
                    
            new FileReader[] {new FileReader(DICT_PATH + File.separator + "passwords.txt")},
            // True for case sensitivity, false otherwise
            false,
            // Dictionaries must be sorted
            new ArraysSort())));
        
        PasswordValidator validator = new PasswordValidator(rule);

        RuleResult result = validator.validate(new PasswordData(password));
        
        return result.isValid();
    }
    
}

