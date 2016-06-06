/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.necs.maximus.db.entity;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
    @NamedQuery(name = "IsSubstitute.findAll", query = "SELECT i FROM IsSubstitute i"),
    @NamedQuery(name = "IsSubstitute.findByPartNumber1", query = "SELECT i FROM IsSubstitute i WHERE i.isSubstitutePK.partNumber1 = :partNumber1"),
    @NamedQuery(name = "IsSubstitute.findByPartNumber2", query = "SELECT i FROM IsSubstitute i WHERE i.isSubstitutePK.partNumber2 = :partNumber2")})
public class IsSubstitute implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected IsSubstitutePK isSubstitutePK;
    @JoinColumn(name = "part_number_1", referencedColumnName = "part_number", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Product product;

    public IsSubstitute() {
    }

    public IsSubstitute(IsSubstitutePK isSubstitutePK) {
        this.isSubstitutePK = isSubstitutePK;
    }

    public IsSubstitute(String partNumber1, String partNumber2) {
        this.isSubstitutePK = new IsSubstitutePK(partNumber1, partNumber2);
    }

    public IsSubstitutePK getIsSubstitutePK() {
        return isSubstitutePK;
    }

    public void setIsSubstitutePK(IsSubstitutePK isSubstitutePK) {
        this.isSubstitutePK = isSubstitutePK;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (isSubstitutePK != null ? isSubstitutePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IsSubstitute)) {
            return false;
        }
        IsSubstitute other = (IsSubstitute) object;
        if ((this.isSubstitutePK == null && other.isSubstitutePK != null) || (this.isSubstitutePK != null && !this.isSubstitutePK.equals(other.isSubstitutePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.IsSubstitute[ isSubstitutePK=" + isSubstitutePK + " ]";
    }

}
