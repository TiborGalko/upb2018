/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z5;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import upb.upb2018.z4.Osoba;

/**
 *
 * @author h
 */
@Entity
@Table(name = "KOMENTAR")
@NamedQueries({
    @NamedQuery(name = "Komentar.findAll", query = "SELECT k FROM Komentar k")
    , @NamedQuery(name = "Komentar.findById", query = "SELECT k FROM Komentar k WHERE k.id = :id")
    , @NamedQuery(name = "Komentar.findByObsah", query = "SELECT k FROM Komentar k WHERE k.obsah LIKE :pattern")})
public class Komentar implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DATUM")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date datum;

    @Column(name = "OBSAH")
    private String obsah;

    @ManyToOne(cascade=CascadeType.REMOVE)
    private Subor parent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getObsah() {
        return obsah;
    }

    public void setObsah(String obsah) {
        this.obsah = obsah;
    }

    public Subor getParent() {
        return parent;
    }

    public void setParent(Subor parent) {
        this.parent = parent;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.datum);
        hash = 79 * hash + Objects.hashCode(this.obsah);             
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
        final Komentar other = (Komentar) obj;
        if (!Objects.equals(this.obsah, other.obsah)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.datum, other.datum)) {
            return false;
        }       
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Komentar{" + "id=" + id + ", datum=" + datum + ", obsah=" + obsah + ", parent=" + parent + '}';
    }

}
