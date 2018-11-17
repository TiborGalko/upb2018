/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class Database {    
    public final static class Result {
        private boolean result;
        private String messsage;
        private Osoba osoba;

        public Result(boolean result, String message, Osoba osoba) {
            this.result = result;
            this.messsage = message;
            this.osoba = osoba;
        }
        
        public Result(boolean result, String message) {
            this.result = result;
            this.messsage = message;
            this.osoba = null;
        }

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public String getMesssage() {
            return messsage;
        }

        public void setMesssage(String messsage) {
            this.messsage = messsage;
        }
        
        public Osoba getOsoba() {
            return this.osoba;
        }
        
        public void setOsoba(Osoba osoba) {
            this.osoba = osoba;
        }

        @Override
        public String toString() {
            return "Result{" + "result=" + result + ", messsage=" + messsage + " ,osoba=" + osoba + "}";
        }
    }
    
    private final EntityManagerFactory emf;
    private final EntityManager em;
    
    public Database() {
        emf = Persistence.createEntityManagerFactory("upb2018PU"); 
        em = emf.createEntityManager();        
    }

    protected Result add(Osoba osoba) {
        if (exist(osoba.getLogin())) {
            return new Result(false, "Osoba uz existuje");
        }        
        persist(osoba);               
        return new Result(true, "Uzivatel uspesne vytvoreny");
    }
    
    protected Osoba get(String meno) {
        Result r = find(meno);
        if(r.isResult()) {
            return r.getOsoba();
        }
        else {
            return null;
        }
    }

    protected Result find(String meno) {        
        try {            
            TypedQuery<Osoba> q = em.createNamedQuery("Osoba.findByMeno", Osoba.class);
            q.setParameter("login", meno);

            if (q.getResultList().size() > 0) {
                for(Osoba o : q.getResultList()) {
                    return new Result(true, "Osoba sa nasla", o);
                }                
            } else {      
                return new Result(false, "Zadana osoba neexistuje");
            }
        } catch (Exception e) {            
            System.err.println("Pri nacitani osob z databazy nastala chyba " + e.getLocalizedMessage());
        } finally {
            em.close();
        }
        
        return new Result(false, "Zadana osoba neexistuje");
    }

    protected boolean exist(String meno) {        
        return find(meno).isResult();
    }

    public void persist(Object object) {
        try {
            em.getTransaction().begin();
            em.persist(object);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Exception pri vytvarani uzivatela " + e.getLocalizedMessage());          
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}
