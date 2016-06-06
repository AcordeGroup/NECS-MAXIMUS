/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.necs.maximus.db.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos Moh
 */
@Entity
@Table(name = "manage")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Manage.findAll", query = "SELECT m FROM Manage m"),
    @NamedQuery(name = "Manage.findByIdQuote", query = "SELECT m FROM Manage m WHERE m.managePK.idQuote = :idQuote"),
    @NamedQuery(name = "Manage.findByIdAgent", query = "SELECT m FROM Manage m WHERE m.managePK.idAgent = :idAgent"),
    @NamedQuery(name = "Manage.findByNotes", query = "SELECT m FROM Manage m WHERE m.managePK.notes = :notes"),
    @NamedQuery(name = "Manage.findByAssignmentDate", query = "SELECT m FROM Manage m WHERE m.assignmentDate = :assignmentDate")})
public class Manage implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ManagePK managePK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "assignment_date")
    @Temporal(TemporalType.DATE)
    private Date assignmentDate;
    @JoinColumn(name = "id_agent", referencedColumnName = "id_agent", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Agent agent;
    @JoinColumn(name = "id_quote", referencedColumnName = "id_quote", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Quote quote;

    public Manage() {
    }

    public Manage(ManagePK managePK) {
        this.managePK = managePK;
    }

    public Manage(ManagePK managePK, Date assignmentDate) {
        this.managePK = managePK;
        this.assignmentDate = assignmentDate;
    }

    public Manage(int idQuote, String idAgent, String notes) {
        this.managePK = new ManagePK(idQuote, idAgent, notes);
    }

    public ManagePK getManagePK() {
        return managePK;
    }

    public void setManagePK(ManagePK managePK) {
        this.managePK = managePK;
    }

    public Date getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(Date assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (managePK != null ? managePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Manage)) {
            return false;
        }
        Manage other = (Manage) object;
        if ((this.managePK == null && other.managePK != null) || (this.managePK != null && !this.managePK.equals(other.managePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.Manage[ managePK=" + managePK + " ]";
    }

}
