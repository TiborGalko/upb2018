/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z5;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import upb.upb2018.z4.Osoba;

/**
 *
 * @author h
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Subor.findAll", query = "SELECT s FROM Subor s")
    , @NamedQuery(name = "Subor.findById", query = "SELECT s FROM Subor s WHERE s.id = :id")
    , @NamedQuery(name = "Subor.findByAutorLogin", query = "SELECT s FROM Subor s WHERE s.autor.login = :login")
    , @NamedQuery(name = "Subor.findByNazov", query = "SELECT s FROM Subor s WHERE s.nazov = :nazov")})
public class Subor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "NAZOV")
    private String nazov;

    @ManyToOne()
    private Osoba autor;

    @ManyToMany
    @JoinTable(name = "ZDIELANE_SUBORY",
            joinColumns = @JoinColumn(name = "subor_fk"),
            inverseJoinColumns = @JoinColumn(name = "osoba_fk"))
    private List<Osoba> zdielajuci;
    
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Komentar> komentare;

    public Subor() {
        zdielajuci = new ArrayList<>();
        komentare = new ArrayList<>();
    }

    public Subor(String nazov, Osoba autor) {
        this.nazov = nazov;
        this.autor = autor; 
        zdielajuci = new ArrayList<>();
        komentare = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNazov() {
        return nazov;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

    public Osoba getAutor() {
        return autor;
    }

    public void setAutor(Osoba autor) {
        this.autor = autor;
    }

    public List<Osoba> getZdielajuci() {
        return zdielajuci;
    }

    public void setZdielajuci(List<Osoba> zdielajuci) {
        this.zdielajuci = zdielajuci;
    }

    public List<Komentar> getKomentare() {
        return komentare;
    }

    public void setKomentare(List<Komentar> komentare) {
        this.komentare = komentare;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.nazov);
        hash = 53 * hash + Objects.hashCode(this.autor);
        hash = 53 * hash + Objects.hashCode(this.zdielajuci);
        hash = 53 * hash + Objects.hashCode(this.komentare);
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
        final Subor other = (Subor) obj;
        if (!Objects.equals(this.nazov, other.nazov)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.autor, other.autor)) {
            return false;
        }        
        return true;
    }

    @Override
    public String toString() {
        return "Subor{" + "id=" + id + ", nazov=" + nazov + ", autor=" + autor + ", zdielajuci=" + zdielajuci + ", komentare=" + komentare + '}';
    }
    

}
