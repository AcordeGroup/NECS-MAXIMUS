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
public class ManagePK implements Serializable {

    @Basic(optional = false)
    @Column(name = "id_quote")
    private int idQuote;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "id_agent")
    private String idAgent;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "notes")
    private String notes;

    public ManagePK() {
    }

    public ManagePK(int idQuote, String idAgent, String notes) {
        this.idQuote = idQuote;
        this.idAgent = idAgent;
        this.notes = notes;
    }

    public int getIdQuote() {
        return idQuote;
    }

    public void setIdQuote(int idQuote) {
        this.idQuote = idQuote;
    }

    public String getIdAgent() {
        return idAgent;
    }

    public void setIdAgent(String idAgent) {
        this.idAgent = idAgent;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idQuote;
        hash += (idAgent != null ? idAgent.hashCode() : 0);
        hash += (notes != null ? notes.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ManagePK)) {
            return false;
        }
        ManagePK other = (ManagePK) object;
        if (this.idQuote != other.idQuote) {
            return false;
        }
        if ((this.idAgent == null && other.idAgent != null) || (this.idAgent != null && !this.idAgent.equals(other.idAgent))) {
            return false;
        }
        if ((this.notes == null && other.notes != null) || (this.notes != null && !this.notes.equals(other.notes))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.ManagePK[ idQuote=" + idQuote + ", idAgent=" + idAgent + ", notes=" + notes + " ]";
    }

}
