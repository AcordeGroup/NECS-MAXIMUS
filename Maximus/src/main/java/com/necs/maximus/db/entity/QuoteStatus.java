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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos Moh
 */
@Entity
@Table(name = "quote_status")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "QuoteStatus.findAll", query = "SELECT q FROM QuoteStatus q"),
    @NamedQuery(name = "QuoteStatus.findByIdQuoteStatus", query = "SELECT q FROM QuoteStatus q WHERE q.idQuoteStatus = :idQuoteStatus"),
    @NamedQuery(name = "QuoteStatus.findByStatus", query = "SELECT q FROM QuoteStatus q WHERE q.status = :status"),
    @NamedQuery(name = "QuoteStatus.findByInitDate", query = "SELECT q FROM QuoteStatus q WHERE q.initDate = :initDate"),
    @NamedQuery(name = "QuoteStatus.findByEndDate", query = "SELECT q FROM QuoteStatus q WHERE q.endDate = :endDate")})
public class QuoteStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_quote_status")
    private Integer idQuoteStatus;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "status")
    private String status;
    @Basic(optional = false)
    @NotNull
    @Column(name = "init_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date initDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.TIME)
    private Date endDate;
    @JoinColumn(name = "id_quote", referencedColumnName = "id_quote")
    @ManyToOne(optional = false)
    private Quote idQuote;

    public QuoteStatus() {
    }

    public QuoteStatus(Integer idQuoteStatus) {
        this.idQuoteStatus = idQuoteStatus;
    }

    public QuoteStatus(Integer idQuoteStatus, String status, Date initDate) {
        this.idQuoteStatus = idQuoteStatus;
        this.status = status;
        this.initDate = initDate;
    }

    public Integer getIdQuoteStatus() {
        return idQuoteStatus;
    }

    public void setIdQuoteStatus(Integer idQuoteStatus) {
        this.idQuoteStatus = idQuoteStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Quote getIdQuote() {
        return idQuote;
    }

    public void setIdQuote(Quote idQuote) {
        this.idQuote = idQuote;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idQuoteStatus != null ? idQuoteStatus.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof QuoteStatus)) {
            return false;
        }
        QuoteStatus other = (QuoteStatus) object;
        if ((this.idQuoteStatus == null && other.idQuoteStatus != null) || (this.idQuoteStatus != null && !this.idQuoteStatus.equals(other.idQuoteStatus))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.QuoteStatus[ idQuoteStatus=" + idQuoteStatus + " ]";
    }

}
