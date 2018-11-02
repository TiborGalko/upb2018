/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author h
 */
public class CryptoUtils {
    private static String ALGORITHM = "AES";
    private static String  TRANSFORMATION = "AES";
    
    public static void encryptAES(String key, File inputFile, File outputFile) throws Exception {
        ALGORITHM = "AES";
        TRANSFORMATION = "AES";
        doFileCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }
    
    public static void decryptAES(String key, File inputFile, File outputFile) throws Exception {
        ALGORITHM = "AES";
        TRANSFORMATION = "AES";
        doFileCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }
    
    public static void encryptRSA(String key, String message, File outputFile) throws Exception {
        ALGORITHM = "RSA";
        doCrypto(Cipher.ENCRYPT_MODE, key, message, outputFile);
    }
    
    public static void decryptRSA(String key, String message, File outputFile) throws Exception {
        ALGORITHM = "RSA";
        doCrypto(Cipher.DECRYPT_MODE, key, message, outputFile);
    }
    
    private static void doFileCrypto(int cipherMode, String key, File inputFile, File outputFile) throws Exception {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            
            byte[] outputBytes = cipher.doFinal(inputBytes);
            
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
            
            inputStream.close();
            outputStream.close();
            
        } catch(NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException ex) {
            throw new Exception("Error encrypting / decrypting file" + ex.getMessage());
        
        }
    }
    
    private static void doCrypto(int cipherMode, String key, String message, File outputFile) throws Exception {     

        try{
            if(cipherMode == Cipher.ENCRYPT_MODE) {
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
        }
        catch(Exception e){
            e.printStackTrace();
        }      
    }
}


