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
@Table(name = "quote_note")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "QuoteNote.findAll", query = "SELECT q FROM QuoteNote q"),
    @NamedQuery(name = "QuoteNote.findByIdQuoteNote", query = "SELECT q FROM QuoteNote q WHERE q.idQuoteNote = :idQuoteNote"),
    @NamedQuery(name = "QuoteNote.findByNote", query = "SELECT q FROM QuoteNote q WHERE q.note = :note"),
    @NamedQuery(name = "QuoteNote.findByCreationDate", query = "SELECT q FROM QuoteNote q WHERE q.creationDate = :creationDate")})
public class QuoteNote implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_quote_note")
    private Integer idQuoteNote;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "note")
    private String note;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @JoinColumn(name = "id_quote", referencedColumnName = "id_quote")
    @ManyToOne(optional = false)
    private Quote idQuote;

    public QuoteNote() {
    }

    public QuoteNote(Integer idQuoteNote) {
        this.idQuoteNote = idQuoteNote;
    }

    public QuoteNote(Integer idQuoteNote, String note, Date creationDate) {
        this.idQuoteNote = idQuoteNote;
        this.note = note;
        this.creationDate = creationDate;
    }

    public Integer getIdQuoteNote() {
        return idQuoteNote;
    }

    public void setIdQuoteNote(Integer idQuoteNote) {
        this.idQuoteNote = idQuoteNote;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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
        hash += (idQuoteNote != null ? idQuoteNote.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof QuoteNote)) {
            return false;
        }
        QuoteNote other = (QuoteNote) object;
        if ((this.idQuoteNote == null && other.idQuoteNote != null) || (this.idQuoteNote != null && !this.idQuoteNote.equals(other.idQuoteNote))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.QuoteNote[ idQuoteNote=" + idQuoteNote + " ]";
    }

}
