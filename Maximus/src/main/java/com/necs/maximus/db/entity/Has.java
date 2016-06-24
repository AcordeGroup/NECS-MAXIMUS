/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.db.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos Moh
 */
@Entity
@Table(name = "has")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Has.findAll", query = "SELECT h FROM Has h"),
    @NamedQuery(name = "Has.findByIdQuote", query = "SELECT h FROM Has h WHERE h.hasPK.idQuote = :idQuote"),
    @NamedQuery(name = "Has.findByPartNumber", query = "SELECT h FROM Has h WHERE h.hasPK.partNumber = :partNumber"),
    @NamedQuery(name = "Has.findByCustomerTargetPrice", query = "SELECT h FROM Has h WHERE h.customerTargetPrice = :customerTargetPrice"),
    @NamedQuery(name = "Has.findByQtyRequested", query = "SELECT h FROM Has h WHERE h.qtyRequested = :qtyRequested"),
    @NamedQuery(name = "Has.findBySuggestedSalesPrice", query = "SELECT h FROM Has h WHERE h.suggestedSalesPrice = :suggestedSalesPrice"),
    @NamedQuery(name = "Has.findByQtyFound", query = "SELECT h FROM Has h WHERE h.qtyFound = :qtyFound")})
public class Has implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected HasPK hasPK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "customer_target_price")
    private BigDecimal customerTargetPrice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "qty_requested")
    private BigDecimal qtyRequested;
    @Column(name = "suggested_sales_price")
    private BigDecimal suggestedSalesPrice;
    @Column(name = "qty_found")
    private BigDecimal qtyFound;
    @Column(name = "observation")
    private String observation;
    @Column(name = "condition")
    private String condition;
    @Column(name = "shipping_cost")
    private BigDecimal shipping_cost;
    @JoinColumn(name = "part_number", referencedColumnName = "part_number", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Product product;
    @JoinColumn(name = "id_quote", referencedColumnName = "id_quote", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Quote quote;

    public Has() {
    }

    public Has(HasPK hasPK) {
        this.hasPK = hasPK;
    }

    public Has(HasPK hasPK, BigDecimal qtyRequested) {
        this.hasPK = hasPK;
        this.qtyRequested = qtyRequested;
    }

    public Has(int idQuote, String partNumber) {
        this.hasPK = new HasPK(idQuote, partNumber);
    }

    public HasPK getHasPK() {
        return hasPK;
    }

    public void setHasPK(HasPK hasPK) {
        this.hasPK = hasPK;
    }

    public BigDecimal getCustomerTargetPrice() {
        return customerTargetPrice;
    }

    public void setCustomerTargetPrice(BigDecimal customerTargetPrice) {
        this.customerTargetPrice = customerTargetPrice;
    }

    public BigDecimal getQtyRequested() {
        return qtyRequested;
    }

    public void setQtyRequested(BigDecimal qtyRequested) {
        this.qtyRequested = qtyRequested;
    }

    public BigDecimal getSuggestedSalesPrice() {
        return suggestedSalesPrice;
    }

    public void setSuggestedSalesPrice(BigDecimal suggestedSalesPrice) {
        this.suggestedSalesPrice = suggestedSalesPrice;
    }

    public BigDecimal getQtyFound() {
        return qtyFound;
    }

    public void setQtyFound(BigDecimal qtyFound) {
        this.qtyFound = qtyFound;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public BigDecimal getShipping_cost() {
        return shipping_cost;
    }

    public void setShipping_cost(BigDecimal shipping_cost) {
        this.shipping_cost = shipping_cost;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (hasPK != null ? hasPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Has)) {
            return false;
        }
        Has other = (Has) object;
        if ((this.hasPK == null && other.hasPK != null) || (this.hasPK != null && !this.hasPK.equals(other.hasPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.Has[ hasPK=" + hasPK + " ]";
    }

}
