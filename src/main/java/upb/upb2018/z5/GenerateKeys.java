/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z5;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GenerateKeys extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {   
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            
            PublicKey pub = kp.getPublic();
            PrivateKey pvt = kp.getPrivate();
            
            String pubString = Base64.getEncoder().encodeToString(pub.getEncoded());
            String pvtString = Base64.getEncoder().encodeToString(pvt.getEncoded());
            
            Gson gson = new Gson();
            Keys keys = new Keys(pubString, pvtString);
            
            String keysString = gson.toJson(keys);
            
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(keysString);
            out.flush();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(GenerateKeys.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}