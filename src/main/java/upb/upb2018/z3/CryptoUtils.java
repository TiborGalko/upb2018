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
import java.nio.charset.StandardCharsets;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
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
    private final static String UPLOAD_PATH = "C:\\Users\\h\\Documents\\upb2018"; // Cesta k priecinku na ukladanie klucov

    public static void encryptAES(String rsaPK, File inputFile, File outputFile) throws Exception {
        ALGORITHM = "AES";
        TRANSFORMATION = "AES/CBC/PKCS5Padding";

        //Generovanie AES kluca                   
        KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
        generator.init(256); // The AES key size in number of bits
        SecretKey key = generator.generateKey();
        
        doFileEncrypt(Cipher.ENCRYPT_MODE, key, rsaPK, inputFile, outputFile);

    }

    public static void decryptAES(String rsaPK, File inputFile, File outputFile) throws Exception {
        ALGORITHM = "AES";
        TRANSFORMATION = "AES/CBC/PKCS5Padding";
        
        // Nacita sa vstupny subor
        byte[] keyFileBytes = FileUtils.readFileToByteArray(inputFile);

        /*Nacitanie IV z ciphertextu*/
        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        System.arraycopy(keyFileBytes, 0, iv, 0, ivSize); // skopirovanie prvych 16 bytov do iv

        int cnt = 0;
        ArrayList<Byte> buffer = new ArrayList<>(); // buffer sa prepise do kluca ked sa najde nieco ine ako 0
        ArrayList<Byte> encryptedKey = new ArrayList<>(); 
        for(int i = ivSize + 1; i < keyFileBytes.length; i++) { //prechadza sa subor a zapisuje sa kluc
            if(cnt == 8) {
                break; // naslo sa 8 nul
            }                
            if(keyFileBytes[i] == 0) {
                cnt++;                               
                buffer.add(keyFileBytes[i]);
            }
            else {
                if(cnt > 0) {
                    for (Byte b : buffer) {
                        encryptedKey.add(b);
                    }
                    buffer.clear();
                }
                else {
                    encryptedKey.add(keyFileBytes[i]);
                }
                cnt = 0;                              
            }         
            //System.arraycopy(keyFileBytes, ivSize + indexOfDivider + 1, encryptedKey, 0, keySize); keyFileBytes[i]
        }                       
        int keySize = encryptedKey.size();
        System.out.println("Key size " + encryptedKey.size());
        
        int textSize = keyFileBytes.length - (ivSize + keySize + 8); 
        byte[] textBytes = new byte[textSize]; // dlzka suboru bez iv
        System.arraycopy(keyFileBytes, ivSize + keySize, textBytes, 0, textSize - 1);
        
        //String fileContent = new String(Files.readAllBytes(Paths.get(inputFile.getPath())));
        System.out.println("zasifrovany aes decrypt kluc: " + encryptedKey.toString());
        String read = doDecrypt(rsaPK, encryptedKey.toString());
        System.out.println("povodny aes decrypt kluc: " + read);
        SecretKey key = new SecretKeySpec(Base64.getDecoder().decode(read), 0, Base64.getDecoder().decode(read).length, "AES");

        doFileDecrypt(Cipher.DECRYPT_MODE, key, iv, inputFile, outputFile);
    }

    private static void doFileEncrypt(int cipherMode, SecretKey key, String rsaPK, File inputFile, File outputFile) throws Exception {
        try {
            //String name = inputFile.getName().substring(0, inputFile.getName().length() - 4);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, key);

            // Nacitanie vlozeneho suboru            
            byte[] inputBytes = FileUtils.readFileToByteArray(inputFile);
            byte[] outputBytes = cipher.doFinal(inputBytes);

            // Get cipher IV 
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

            // Zasifrovanie kluca cez RSA
            String encryptedAESKey = doEncrypt(rsaPK, key);
                        
            System.out.println("aes encrypt kluc: " + Base64.getEncoder().encodeToString(key.getEncoded()));
            System.out.println("zasifrovany aes encrypt kluc: " + encryptedAESKey);
            System.out.println("dlzka zasifrovaneho aes kluca v bytoch: " + encryptedAESKey.getBytes().length);
            // Spojenie iv, zasifrovaneho kluca a ciphertextu do jedneho pola
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(iv);            
            outputStream.write(encryptedAESKey.getBytes());
            outputStream.write(00000000); // 8 nul na oddelenie textu a kluca
            outputStream.write(outputBytes);
            
            byte out[] = outputStream.toByteArray();
            System.out.println(Arrays.toString(out));

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

    private static String doEncrypt(String key, SecretKey message) throws Exception {
        
        System.out.println("Key for encryption " + key);
        System.out.println("Message " + message);

        // convert AES key to String
        String StringMessage = Base64.getEncoder().encodeToString(message.getEncoded());

        //Create RSA public key from string
        PublicKey publicKey = null;
        try {
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

    private static String doDecrypt(String key, String message) throws Exception {

        System.out.println("Key for decryption " + key);
        System.out.println("Message " + message);
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
