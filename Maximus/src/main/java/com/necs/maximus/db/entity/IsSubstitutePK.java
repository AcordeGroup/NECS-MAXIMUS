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
public class IsSubstitutePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "part_number_1")
    private String partNumber1;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "part_number_2")
    private String partNumber2;

    public IsSubstitutePK() {
    }

    public IsSubstitutePK(String partNumber1, String partNumber2) {
        this.partNumber1 = partNumber1;
        this.partNumber2 = partNumber2;
    }

    public String getPartNumber1() {
        return partNumber1;
    }

    public void setPartNumber1(String partNumber1) {
        this.partNumber1 = partNumber1;
    }

    public String getPartNumber2() {
        return partNumber2;
    }

    public void setPartNumber2(String partNumber2) {
        this.partNumber2 = partNumber2;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (partNumber1 != null ? partNumber1.hashCode() : 0);
        hash += (partNumber2 != null ? partNumber2.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IsSubstitutePK)) {
            return false;
        }
        IsSubstitutePK other = (IsSubstitutePK) object;
        if ((this.partNumber1 == null && other.partNumber1 != null) || (this.partNumber1 != null && !this.partNumber1.equals(other.partNumber1))) {
            return false;
        }
        if ((this.partNumber2 == null && other.partNumber2 != null) || (this.partNumber2 != null && !this.partNumber2.equals(other.partNumber2))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.IsSubstitutePK[ partNumber1=" + partNumber1 + ", partNumber2=" + partNumber2 + " ]";
    }

}
