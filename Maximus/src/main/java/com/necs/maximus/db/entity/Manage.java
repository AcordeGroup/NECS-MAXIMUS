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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    @NamedQuery(name = "Manage.findByIdManage", query = "SELECT m FROM Manage m WHERE m.idManage = :idManage"),
    @NamedQuery(name = "Manage.findByIdQuote", query = "SELECT m FROM Manage m WHERE m.idQuote = :idQuote"),
    @NamedQuery(name = "Manage.findByIdAgent", query = "SELECT m FROM Manage m WHERE m.idAgent = :idAgent"),
    @NamedQuery(name = "Manage.findByAssignmentDate", query = "SELECT m FROM Manage m WHERE m.assignmentDate = :assignmentDate")})
public class Manage implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_manage")
    private Integer idManage;
    @Basic(optional = false)
    @NotNull
    @Column(name = "assignment_date")
    @Temporal(TemporalType.DATE)
    private Date assignmentDate;
    @Column(name = "deallocation_date")
    @Temporal(TemporalType.DATE)
    private Date deallocationDate;
    @JoinColumn(name = "id_agent", referencedColumnName = "id_agent")
    @ManyToOne(optional = false)
    private Agent idAgent;
    @JoinColumn(name = "id_quote", referencedColumnName = "id_quote")
    @ManyToOne(optional = false)
    private Quote idQuote;

    public Manage() {
    }

    public Manage(Integer idManage) {
        this.idManage = idManage;
    }

    public Manage(Integer idManage, Date assignmentDate) {
        this.idManage = idManage;
        this.assignmentDate = assignmentDate;
    }

    public Date getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(Date assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public Agent getIdAgent() {
        return idAgent;
    }

    public void setIdAgent(Agent idAgent) {
        this.idAgent = idAgent;
    }

    public Quote getIdQuote() {
        return idQuote;
    }

    public void setIdQuote(Quote idQuote) {
        this.idQuote = idQuote;
    }

    public Date getDeallocationDate() {
        return deallocationDate;
    }

    public void setDeallocationDate(Date deallocationDate) {
        this.deallocationDate = deallocationDate;
    }

    public Integer getIdManage() {
        return idManage;
    }

    public void setIdManage(Integer idManage) {
        this.idManage = idManage;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idManage != null ? idManage.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Manage)) {
            return false;
        }
        Manage other = (Manage) object;
        if ((this.idManage == null && other.idManage != null) || (this.idManage != null && !this.idManage.equals(other.idManage))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.Manage[ idManage=" + idManage + " ]";
    }

}
