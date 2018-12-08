/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z4;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import upb.upb2018.z5.Komentar;
import upb.upb2018.z5.Subor;

public class Database {

    public final static class Result {

        private boolean result;
        private String messsage;
        private Osoba osoba;
        private Subor subor;

        public Result(boolean result, String message, Osoba osoba) {
            this.result = result;
            this.messsage = message;
            this.osoba = osoba;
        }

        public Result(boolean result, String message, Subor subor) {
            this.result = result;
            this.messsage = message;
            this.osoba = null;
            this.subor = subor;
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
    private EntityManager em;

    public Database() {
        emf = Persistence.createEntityManagerFactory("upb2018PU");
    }

    protected Result add(Osoba osoba) {
        if (osobaExist(osoba.getLogin())) {
            return new Result(false, "Osoba uz existuje");
        }
        persist(osoba);
        return new Result(true, "Uzivatel uspesne vytvoreny");
    }

    public Osoba get(String meno) {
        Result r = find(meno);
        System.out.println(r.getMesssage());
        if (r.isResult()) {
            return r.getOsoba();
        } else {
            return null;
        }
    }

    /**
     * Nacita subor pre usera z databazy
     *
     * @param meno username
     * @return Subor object
     */
    public Subor getFile(String meno) {
        EntityManager entityManager;
        entityManager = emf.createEntityManager();
        TypedQuery<Subor> q = entityManager.createNamedQuery("Subor.findByNazov", Subor.class);
        q.setParameter("nazov", meno);
        Subor out;
        if (q.getResultList().size() > 0) {
            out = q.getResultList().get(0);
        } else {
            out = null;
        }
        System.out.println("Ziskava sa subor " + meno + " vysledok " + out);
        entityManager.close();
        return out;
    }

    /**
     * Nacita vsetky subory pre usera z databazy
     *
     * @param login username
     * @return List nazvov suborov
     */
    public List<String> getAllfiles(String login) {
        List<String> ret = new ArrayList();
        EntityManager entityManager;
        entityManager = emf.createEntityManager();
        // Najde subory autora
        TypedQuery<Subor> q = entityManager.createNamedQuery("Subor.findByAutorLogin", Subor.class);
        q.setParameter("login", login);
        if (q.getResultList().size() > 0) {
            for (Subor s : q.getResultList()) {
                ret.add(s.getNazov());
            }
        }

        // Prida aj zdielane subory
        TypedQuery<Subor> q1 = entityManager.createNamedQuery("Subor.findAll", Subor.class);
        if (q1.getResultList().size() > 0) {
            for (Subor s : q1.getResultList()) {
                List<Osoba> o = s.getZdielajuci();
                for (Osoba os : o) {
                    if (os.getLogin().equals(login)) {
                        ret.add(s.getNazov());
                    }
                }
            }
        }
        entityManager.close();
        return ret;
    }

    public Result saveFileToDB(Osoba autor, String fileName) {
        if (autor == null || fileName.isEmpty()) {
            return new Result(false, "Zle zadane argumenty"); //osetrenie aby sa nezapisovali blbosti do db
        }
        Subor subor = new Subor(); // list sa vytvori v konstruktore
        subor.setAutor(autor);
        subor.setNazov(fileName);

        persist(subor);
        return new Result(true, "Subor uspesne pridany");
    }

    public Result deleteFile(String filename) {
        if (filename == null) {
            return new Result(false, "Zle zadane argumenty"); //osetrenie aby sa nezapisovali blbosti do db
        }
        try {
            em = emf.createEntityManager();
            TypedQuery<Subor> q = em.createNamedQuery("Subor.findByNazov", Subor.class);
            q.setParameter("nazov", filename);
            Subor s;
            if (q.getResultList().size() > 0) {
                s = q.getResultList().get(0);
                em.getTransaction().begin();
                em.remove(s);
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            System.err.println("Pri nacitani suborov z databazy nastala chyba " + e.getLocalizedMessage());
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return new Result(true, "Vymazanie uspesne");
    }

    public Result addPrijemcaToFile(String fileName, Osoba prijemca) {
        try {
            em = emf.createEntityManager();
            TypedQuery<Subor> q = em.createNamedQuery("Subor.findByNazov", Subor.class);
            q.setParameter("nazov", fileName);
            if (q.getResultList().size() > 0) {
                for (Subor s : q.getResultList()) {
                    em.getTransaction().begin();
                    s.getZdielajuci().add(prijemca);
                    em.getTransaction().commit();
                    return new Result(true, "Subor uspesne zdielany");
                }
            } else {
                return new Result(false, "Subor neexistuje");
            }
        } catch (Exception e) {
            System.err.println("Pri nacitani suborov z databazy nastala chyba " + e.getLocalizedMessage());
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return new Result(false, "Chyba");
    }

    public Result addComment(String filename, String komentar, Osoba autor) {
        if (filename == null || komentar == null) {
            return new Result(false, "Zle parametre");
        }
        Subor s = getFile(filename);
        Komentar k = new Komentar();
        k.setAutor(autor);
        k.setParent(s);
        k.setDatum(new Date());
        k.setObsah(komentar);
        autor.getKomentare().add(k);
        persist(k);
        return new Result(true, "Komentar pridany");
    }

    /**
     * Ak nastane chyba list ma velkost nula
     *
     * @param fileName
     * @return
     */
    public List<String> getAllCommentsByFileName(String fileName) {
        List<String> list = new ArrayList<>();
        EntityManager entityManager;
        entityManager = emf.createEntityManager();       

        Subor s = getFile(fileName);

        if (s != null) {
            TypedQuery<Komentar> q = entityManager.createNamedQuery("Komentar.findAll", Komentar.class);
            if (q.getResultList().size() > 0) {
                for (Komentar k : q.getResultList()) {                    
                    if (k.getParent().equals(s)) {
                        list.add(k.getObsah());
                    }
                }
            }
        }
        entityManager.close();
        return list;
    }

    public String checkFileName(String fileName) {                           
        String[] tokens;
        String newName = fileName; // zobere nazov
        StringBuilder builder = new StringBuilder();

        int i = 1;
        while (getFile(newName) != null) {
            tokens = newName.split("\\."); // rozdeli cez bodky
            if (i > 1) {
                tokens[0] = tokens[0].substring(0, tokens[0].length() - 4); // odstrani cislovanie ak bolo pridane
            }
            tokens[0] += " (" + i + ")";

            for (int j = 0; j < tokens.length; j++) {
                builder.append(tokens[j]); // vytvori novy string
                if(j < tokens.length - 1) {
                    builder.append(".");
                }
            }
            newName = builder.toString();
            builder.setLength(0); // vymazanie buildera
            i++;
        }
        return newName;
    }

    private Result find(String meno) {
        try {
            em = emf.createEntityManager();
            TypedQuery<Osoba> q = em.createNamedQuery("Osoba.findByMeno", Osoba.class);
            q.setParameter("login", meno);
            if (q.getResultList().size() > 0) {
                for (Osoba o : q.getResultList()) {
                    System.out.println(o);
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

        return new Result(false, "Chyba");
    }

    private boolean osobaExist(String meno) {
        return find(meno).isResult();
    }

    public void persist(Object object) {
        try {
            em = emf.createEntityManager();
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
