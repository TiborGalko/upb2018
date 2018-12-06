/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z4;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import upb.upb2018.z5.Subor;

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
        try {
            em = emf.createEntityManager();
            TypedQuery<Subor> q = em.createNamedQuery("Subor.findByMeno", Subor.class);
            q.setParameter("nazov", meno);
            if (q.getResultList().size() > 0) {
                return q.getResultList().get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("Pri nacitani suboru z databazy nastala chyba " + e.getLocalizedMessage());
        } finally {
            em.close();
        }
        return null;
    }

    /**
     * Nacita vsetky subory pre usera z databazy
     *
     * @param login username
     * @return List nazvov suborov
     */
    public List<String> getAllfiles(String login) {
        List<String> ret = new ArrayList();
        try {
            em = emf.createEntityManager();
            TypedQuery<Subor> q = em.createNamedQuery("Subor.findByOsoba", Subor.class);
            q.setParameter("login", login);
            if (q.getResultList().size() > 0) {
                for (Subor s : q.getResultList()) {
                    ret.add(s.getNazov());
                }
            }

            TypedQuery<Subor> q1 = em.createNamedQuery("Subor.findAll", Subor.class);
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
        } catch (Exception e) {
            System.err.println("Pri nacitani suboru z databazy nastala chyba " + e.getLocalizedMessage());
        } finally {
            em.close();
        }
        return ret;
    }

    public Result saveFileToDB(Osoba autor, String fileName, Osoba prijemca) {
        if(autor == null || fileName.isEmpty() || prijemca == null) {
            return new Result(false, "Zle zadane argumenty"); //osetrenie aby sa nezapisovali blbosti do db
        }
        Subor subor = new Subor(); // list sa vytvori v konstruktore
        subor.setAutor(autor);
        subor.setNazov(fileName);
        subor.getZdielajuci().add(prijemca);
        persist(subor);
        return new Result(true, "Subor uspesne pridany");
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
