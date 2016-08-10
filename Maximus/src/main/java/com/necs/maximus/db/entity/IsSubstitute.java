/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.db.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos Moh
 */
@Entity
@Table(name = "is_substitute")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "IsSubstitute.findAll", query = "SELECT i FROM IsSubstitute i")})
public class IsSubstitute implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_substitute")
    private Integer idSubstitute;
    @JoinColumn(name = "part_number_base", referencedColumnName = "part_number")
    @ManyToOne(optional = false)
    private Product partNumberBase;
    @JoinColumn(name = "part_number_substitute", referencedColumnName = "part_number")
    @ManyToOne(optional = false)
    private Product partNumberSubstitute;

    public IsSubstitute() {
    }

    public IsSubstitute(Integer idSubstitute) {
        this.idSubstitute = idSubstitute;
    }

    public Integer getIdSubstitute() {
        return idSubstitute;
    }

    public void setIdSubstitute(Integer idSubstitute) {
        this.idSubstitute = idSubstitute;
    }

    public Product getPartNumberBase() {
        return partNumberBase;
    }

    public void setPartNumberBase(Product partNumberBase) {
        this.partNumberBase = partNumberBase;
    }

    public Product getPartNumberSubstitute() {
        return partNumberSubstitute;
    }

    public void setPartNumberSubstitute(Product partNumberSubstitute) {
        this.partNumberSubstitute = partNumberSubstitute;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idSubstitute != null ? idSubstitute.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IsSubstitute)) {
            return false;
        }
        IsSubstitute other = (IsSubstitute) object;
        if ((this.idSubstitute == null && other.idSubstitute != null) || (this.idSubstitute != null && !this.idSubstitute.equals(other.idSubstitute))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.IsSubstitute[ idSubstitute=" + idSubstitute + " ]";
    }

}
