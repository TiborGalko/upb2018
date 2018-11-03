/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.apache.commons.io.FileUtils;

/**
 * https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Cipher
 *
 * @author Karasek, Galko, Sokolova, Krist
 */
public class CryptoUtils {

    private static String ALGORITHM = "AES";
    private static String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private final static String UPLOAD_PATH = "C:\\Users\\h\\Documents\\upb2018"; // Cesta k priecinku na ukladanie klucov

    public static void encryptAES(File inputFile, File outputFile) throws Exception {
        ALGORITHM = "AES";
        TRANSFORMATION = "AES/CBC/PKCS5Padding";

        String name = inputFile.getName().substring(0, inputFile.getName().length() - 4);

        //Generovanie AES kluca                   
        KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
        generator.init(256); // The AES key size in number of bits
        SecretKey key = generator.generateKey();

        File folder = new File(UPLOAD_PATH);
        File result = new File(folder, name + "-key");
                
        FileOutputStream out = new FileOutputStream(result);
        ObjectOutputStream oout = new ObjectOutputStream(out);
        
        oout.writeObject(key);

        doFileEncrypt(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);       
    }

    public static void decryptAES(File inputFile, File outputFile) throws Exception {
        ALGORITHM = "AES";
        TRANSFORMATION = "AES/CBC/PKCS5Padding";

        String name = inputFile.getName().substring(0, inputFile.getName().length() - 4);
 
        if (name.contains(".")) {            
            String[] parts = name.split(Pattern.quote("."));
            name = parts[0];
        } else {
            throw new IllegalArgumentException("String " + name + " does not contain -");
        }

        File folder = new File(UPLOAD_PATH);
        File resultKey = new File(folder, name + "-key"); // key je vzdy outputFile + "-key"        
        FileInputStream in = new FileInputStream(resultKey);
        
        SecretKey key = null;
        try (ObjectInputStream ois = new ObjectInputStream(in)) {
            key = (SecretKey) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        in.close();

        File resultIV = new File(folder, name + "-iv");        
        byte[] iv = FileUtils.readFileToByteArray(resultIV);
        
        doFileDecrypt(Cipher.DECRYPT_MODE, key, iv, inputFile, outputFile);
    }
    
    public static void encryptRSA(String message, File outputFile) throws Exception {
        ALGORITHM = "RSA";
        String key = "todo";
        doCrypto(Cipher.ENCRYPT_MODE, key, message, outputFile);
    }

    public static void decryptRSA(String message, File outputFile) throws Exception {
        ALGORITHM = "RSA";
        String key = "todo";
        doCrypto(Cipher.DECRYPT_MODE, key, message, outputFile);
    }             

    private static void doFileEncrypt(int cipherMode, SecretKey key, File inputFile, File outputFile) throws Exception {
        try {
            String name = inputFile.getName().substring(0, inputFile.getName().length() - 4);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, key);

            // Nacitanie vlozeneho suboru            
            byte[] inputBytes = FileUtils.readFileToByteArray(inputFile);
            byte[] outputBytes = cipher.doFinal(inputBytes);
            // Vypisanie zasifrovaneho textu
            FileUtils.writeByteArrayToFile(outputFile, outputBytes);

            // Get cipher IV 
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            
            // Vypisanie iv do suboru
            File folder = new File(UPLOAD_PATH);
            File resultIV = new File(folder, name + "-iv");
            FileUtils.writeByteArrayToFile(resultIV, iv);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException ex) {
            throw new Exception("Error encrypting / decrypting file" + ex.getMessage());
        }
    }

    private static void doFileDecrypt(int cipherMode, SecretKey key, byte[] iv, File inputFile, File outputFile) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, key, new IvParameterSpec(iv));

            // Nacitanie vlozeneho suboru
            byte[] inputBytes = FileUtils.readFileToByteArray(inputFile);            
            byte[] outputBytes = cipher.doFinal(inputBytes);                        

            FileUtils.writeByteArrayToFile(outputFile, outputBytes);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException ex) {
            throw new Exception("Error encrypting / decrypting file" + ex.getMessage());
        }
    }
    
    private static void doCrypto(int cipherMode, String key, String message, File outputFile) throws Exception {

        try {
            if (cipherMode == Cipher.ENCRYPT_MODE) {
                byte[] byteKey = Base64.getDecoder().decode(key.replace(" ", ""));
                X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
                KeyFactory kf = KeyFactory.getInstance(ALGORITHM);

                PublicKey pk = kf.generatePublic(X509publicKey);

                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, pk);
                String ret = Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes("UTF-8")));

                try (PrintStream out = new PrintStream(new FileOutputStream(outputFile))) {
                    out.print(ret);
                }
            } else {
                System.out.println("---------------------------------------------------");
                byte[] byteKey = Base64.getDecoder().decode(key.replace(" ", ""));
                PKCS8EncodedKeySpec pkcs8encodedkeyspec = new PKCS8EncodedKeySpec(byteKey);
                KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
                PrivateKey sk = kf.generatePrivate(pkcs8encodedkeyspec);

                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, sk);
                String ret = Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes("UTF-8")));

                System.out.println(ret);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
