/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import upb.upb2018.z5.Komentar;
import upb.upb2018.z5.Subor;

/**
 *
 * @author h
 */
@Entity
@Table(name = "OSOBA")
@NamedQueries({
    @NamedQuery(name = "Osoba.findAll", query = "SELECT o FROM Osoba o")
    , @NamedQuery(name = "Osoba.findById", query = "SELECT o FROM Osoba o WHERE o.id = :id")
    , @NamedQuery(name = "Osoba.findByMeno", query = "SELECT o FROM Osoba o WHERE o.login = :login")})
public class Osoba implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;    
    
    @Column(name = "LOGIN")
    private String login;
    
    @Column(name = "PASSWORD")
    private String password;
    
    @Column(name = "SALT")
    private long salt;
    
    @Column(name = "PUBKEY",columnDefinition="LONGTEXT")
    private String pubKey;
    
    @Column(name = "PRIVKEY",columnDefinition="LONGTEXT")
    private String privKey;
    
    @OneToMany(mappedBy = "autor", cascade = CascadeType.REMOVE)
    private List<Subor> subory;
     
    @ManyToMany(mappedBy = "zdielajuci", cascade = CascadeType.REMOVE)
    private List<Subor> zdielaneSubory;
    
    @OneToMany(mappedBy = "autor")
    private List<Komentar> komentare;
    
    public Osoba() {        
    }    
    public Osoba(String login, String password, long salt, String pubKey, String privKey) {
        this.login = login;
        this.password = password;
        this.salt = salt;
        this.pubKey = pubKey;
        this.privKey = privKey;
        subory = new ArrayList<>();
        zdielaneSubory = new ArrayList<>();
        komentare = new ArrayList<>();
    }  

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getSalt() {
        return salt;
    }

    public void setSalt(long salt) {
        this.salt = salt;
    }

    public List<Subor> getSubory() {
        return subory;
    }

    public void setSubory(List<Subor> subory) {
        this.subory = subory;
    }

    public List<Subor> getZdielaneSubory() {
        return zdielaneSubory;
    }

    public void setZdielaneSubory(List<Subor> zdielaneSubory) {
        this.zdielaneSubory = zdielaneSubory;
    }   
        
    public List<Komentar> getKomentare() {
        return komentare;
    }

    public void setKomentare(List<Komentar> komentare) {
        this.komentare = komentare;
    } 

    public String getPubKey() {
        return pubKey;
    }
    
    public String getPrivKey() {
        return privKey;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.id);
        hash = 11 * hash + Objects.hashCode(this.login);
        hash = 11 * hash + Objects.hashCode(this.password);
        hash = 11 * hash + (int) (this.salt ^ (this.salt >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Osoba other = (Osoba) obj;
        if (this.salt != other.salt) {
            return false;
        }
        if (!Objects.equals(this.login, other.login)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }        
        return true;
    }

    @Override
    public String toString() {
        return "Osoba{" + "id=" + id + ", login=" + login + ", password=" + password + ", salt=" + salt + ", subory=" + subory + ", zdielaneSubory=" + zdielaneSubory + ", komentare=" + komentare + '}';
    }    
  
}
