/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.necs.maximus.db.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Carlos Moh
 */
@Embeddable
public class HasPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id_quote")
    private int idQuote;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "part_number")
    private String partNumber;

    public HasPK() {
    }

    public HasPK(int idQuote, String partNumber) {
        this.idQuote = idQuote;
        this.partNumber = partNumber;
    }

    public int getIdQuote() {
        return idQuote;
    }

    public void setIdQuote(int idQuote) {
        this.idQuote = idQuote;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idQuote;
        hash += (partNumber != null ? partNumber.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HasPK)) {
            return false;
        }
        HasPK other = (HasPK) object;
        if (this.idQuote != other.idQuote) {
            return false;
        }
        if ((this.partNumber == null && other.partNumber != null) || (this.partNumber != null && !this.partNumber.equals(other.partNumber))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.HasPK[ idQuote=" + idQuote + ", partNumber=" + partNumber + " ]";
    }

}
