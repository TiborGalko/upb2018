/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.io.FileUtils;

/**
 * https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Cipher
 *
 * @author Karasek, Galko, Sokolova, Krist
 */
public class CryptoUtils {

    private static String ALGORITHM = "AES";
    private static String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private final static String UPLOAD_PATH = "C:\\Users\\Jurko\\Documents\\skola\\UPB\\upb2018\\files"; // Cesta k priecinku na ukladanie klucov

    public static void encryptAES(String rsaPK, File inputFile, File outputFile) throws Exception {
        ALGORITHM = "AES";
        TRANSFORMATION = "AES/CBC/PKCS5Padding";

        String name = inputFile.getName().substring(0, inputFile.getName().length() - 4);

        //Generovanie AES kluca                   
        KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
        generator.init(256); // The AES key size in number of bits
        SecretKey key = generator.generateKey();

        File folder = new File(UPLOAD_PATH);
        File result = new File(folder, name + "-key");

/*
        FileOutputStream out = new FileOutputStream(result);
        ObjectOutputStream oout = new ObjectOutputStream(out);
        oout.writeObject(key);
*/
                
        //FileOutputStream out = new FileOutputStream(result);
        //ObjectOutputStream oout = new ObjectOutputStream(out);
        
        String write = doEncrypt(Cipher.ENCRYPT_MODE, rsaPK, key);
        System.out.println("aes kluc: " + Base64.getEncoder().encodeToString(key.getEncoded()));
        System.out.println("zasifrovany aes kluc: " + write);
        try (PrintStream out = new PrintStream(new FileOutputStream(result))) {
            out.print(write);
        }

        doFileEncrypt(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    public static void decryptAES(String rsaPK, File inputFile, File outputFile) throws Exception {
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

        File resultKey = new File(folder, name + "-key");               
        /*byte[] keyFileBytes = FileUtils.readFileToByteArray(resultKey);
        SecretKey key = null;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(keyFileBytes))) {
            key = (SecretKey) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
        
        String fileContent = new String(Files.readAllBytes(Paths.get(resultKey.getPath())));
        System.out.println("zasifrovany aes kluc: " + fileContent);
        String read = doDecrypt(Cipher.ENCRYPT_MODE, rsaPK, fileContent);
        System.out.println("povodny aes kluc: " + read);
        SecretKey key = new SecretKeySpec(Base64.getDecoder().decode(read), 0, Base64.getDecoder().decode(read).length, "AES");

        /*Nacitanie IV z ciphertextu*/
        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        System.arraycopy(keyFileBytes, 0, iv, 0, ivSize); // skopirovanie prvych 16 bytov do iv

        int keySize = keyFileBytes.length - ivSize;
        byte[] keyBytes = new byte[keySize]; // dlzka suboru bez iv
        System.arraycopy(keyFileBytes, ivSize + 1, keyBytes, 0, keySize - 1);

        doFileDecrypt(Cipher.DECRYPT_MODE, key, iv, inputFile, outputFile);
        
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

            // Get cipher IV 
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

            // Spojenie iv a ciphertextu do jedneho pola
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(iv);
            outputStream.write(outputBytes);
            byte out[] = outputStream.toByteArray();

            FileUtils.writeByteArrayToFile(outputFile, out);

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
    
    private static String doEncrypt(int cipherMode, String key, SecretKey message) throws Exception {
        
        // convert AES key to String
        String StringMessage = Base64.getEncoder().encodeToString(message.getEncoded());
        
        //Create RSA public key from string
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(key.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(StringMessage.getBytes()));
    }
    
    private static String doDecrypt(int cipherMode, String key, String message) throws Exception {
        
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        return new String(cipher.doFinal(Base64.getDecoder().decode(message.getBytes())));
    }
    
}
