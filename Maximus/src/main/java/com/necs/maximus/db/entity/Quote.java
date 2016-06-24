/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.db.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Carlos Moh
 */
@Entity
@Table(name = "quote")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Quote.findAll", query = "SELECT q FROM Quote q"),
    @NamedQuery(name = "Quote.findByIdQuote", query = "SELECT q FROM Quote q WHERE q.idQuote = :idQuote"),
    @NamedQuery(name = "Quote.findByContact", query = "SELECT q FROM Quote q WHERE q.contact = :contact"),
    @NamedQuery(name = "Quote.findByEmail", query = "SELECT q FROM Quote q WHERE q.email = :email"),
    @NamedQuery(name = "Quote.findByCreationDate", query = "SELECT q FROM Quote q WHERE q.creationDate = :creationDate")})
public class Quote implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_quote")
    private Integer idQuote;
    @Size(max = 50)
    @Column(name = "contact")
    private String contact;
    @Size(max = 30)
    @Column(name = "email")
    private String email;
    @Size(max = 300)
    @Column(name = "shipping_to")
    private String shipping_to;
    @Column(name = "shipping_cost")
    private BigDecimal shipping_cost;
    @Column(name = "include_shipping_cost")
    private Integer include_shipping_cost;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation_date")
    @Temporal(TemporalType.DATE)
    private Date creationDate;
    @JoinColumn(name = "id_agent", referencedColumnName = "id_agent")
    @ManyToOne(optional = false)
    private Agent idAgent;
    @JoinColumn(name = "id_customer", referencedColumnName = "id_customer")
    @ManyToOne(optional = false)
    private Customer idCustomer;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quote")
    private List<Has> hasList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idQuote")
    private List<QuoteNote> quoteNoteList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idQuote")
    private List<QuoteStatus> quoteStatusList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quote")
    private List<Manage> manageList;

    public Quote() {
    }

    public Quote(Integer idQuote) {
        this.idQuote = idQuote;
    }

    public Quote(Integer idQuote, Date creationDate) {
        this.idQuote = idQuote;
        this.creationDate = creationDate;
    }

    public Integer getIdQuote() {
        return idQuote;
    }

    public void setIdQuote(Integer idQuote) {
        this.idQuote = idQuote;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Agent getIdAgent() {
        return idAgent;
    }

    public void setIdAgent(Agent idAgent) {
        this.idAgent = idAgent;
    }

    public Customer getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(Customer idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getShipping_to() {
        return shipping_to;
    }

    public void setShipping_to(String shipping_to) {
        this.shipping_to = shipping_to;
    }

    public BigDecimal getShipping_cost() {
        return shipping_cost;
    }

    public void setShipping_cost(BigDecimal shipping_cost) {
        this.shipping_cost = shipping_cost;
    }

    public Integer getInclude_shipping_cost() {
        return include_shipping_cost;
    }

    public void setInclude_shipping_cost(Integer include_shipping_cost) {
        this.include_shipping_cost = include_shipping_cost;
    }

    @XmlTransient
    public List<Has> getHasList() {
        return hasList;
    }

    public void setHasList(List<Has> hasList) {
        this.hasList = hasList;
    }

    @XmlTransient
    public List<QuoteNote> getQuoteNoteList() {
        return quoteNoteList;
    }

    public void setQuoteNoteList(List<QuoteNote> quoteNoteList) {
        this.quoteNoteList = quoteNoteList;
    }

    @XmlTransient
    public List<QuoteStatus> getQuoteStatusList() {
        return quoteStatusList;
    }

    public void setQuoteStatusList(List<QuoteStatus> quoteStatusList) {
        this.quoteStatusList = quoteStatusList;
    }

    @XmlTransient
    public List<Manage> getManageList() {
        return manageList;
    }

    public void setManageList(List<Manage> manageList) {
        this.manageList = manageList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idQuote != null ? idQuote.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Quote)) {
            return false;
        }
        Quote other = (Quote) object;
        if ((this.idQuote == null && other.idQuote != null) || (this.idQuote != null && !this.idQuote.equals(other.idQuote))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.Quote[ idQuote=" + idQuote + " ]";
    }

}
