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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    @NamedQuery(name = "Has.findByIdQuote", query = "SELECT h FROM Has h WHERE h.quote.idQuote = :idQuote"),
    @NamedQuery(name = "Has.findByPartNumber", query = "SELECT h FROM Has h WHERE h.product.partNumber = :partNumber"),
    @NamedQuery(name = "Has.findByCustomerTargetPrice", query = "SELECT h FROM Has h WHERE h.customerTargetPrice = :customerTargetPrice"),
    @NamedQuery(name = "Has.findByQtyRequested", query = "SELECT h FROM Has h WHERE h.qtyRequested = :qtyRequested"),
    @NamedQuery(name = "Has.findBySuggestedSalesPrice", query = "SELECT h FROM Has h WHERE h.suggestedSalesPrice = :suggestedSalesPrice"),
    @NamedQuery(name = "Has.findByQtyFound", query = "SELECT h FROM Has h WHERE h.qtyFound = :qtyFound")})
public class Has implements Serializable {
   
   

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "has_id")
    private Integer hasId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "qty_requested")
    private Integer qtyRequested;
    @Column(name = "qty_found")
    private Integer qtyFound;
    @Column(name = "customer_target_price")
    private BigDecimal customerTargetPrice;
    @Column(name = "suggested_sales_price")
    private BigDecimal suggestedSalesPrice;
    @Column(name = "observation")
    private String observation;
    @Column(name = "extended")
    private BigDecimal extended;
    @Column(name = "shipping_cost")
    private BigDecimal shipping_cost;
    @JoinColumn(name = "part_number", referencedColumnName = "part_number")
    @ManyToOne(optional = false)
    private Product product;
    @JoinColumn(name = "id_quote", referencedColumnName = "id_quote")
    @ManyToOne(optional = false)
    private Quote quote;
    @JoinColumn(name = "id_vendor", referencedColumnName = "id_vendor")
    @ManyToOne(optional = true)
    private Vendor idVendor;
    @JoinColumn(name = "condition_type", referencedColumnName = "id_condition_type")
    @ManyToOne(optional = false)
    private ConditionType conditionType;

    public Has() {
    }

    public Has(Integer hasId) {
        this.hasId = hasId;
    }

    public Has(Integer hasId, Integer qtyRequested) {
        this.hasId = hasId;
        this.qtyRequested = qtyRequested;
    }

    public BigDecimal getCustomerTargetPrice() {
        return customerTargetPrice;
    }

    public void setCustomerTargetPrice(BigDecimal customerTargetPrice) {
        this.customerTargetPrice = customerTargetPrice;
    }

    public Integer getQtyRequested() {
        return qtyRequested;
    }

    public void setQtyRequested(Integer qtyRequested) {
        this.qtyRequested = qtyRequested;
    }

    public BigDecimal getSuggestedSalesPrice() {
        return suggestedSalesPrice;
    }

    public void setSuggestedSalesPrice(BigDecimal suggestedSalesPrice) {
        this.suggestedSalesPrice = suggestedSalesPrice;
    }

    public Integer getQtyFound() {
        return qtyFound;
    }

    public void setQtyFound(Integer qtyFound) {
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

    public BigDecimal getShipping_cost() {
        return shipping_cost;
    }

    public void setShipping_cost(BigDecimal shipping_cost) {
        this.shipping_cost = shipping_cost;
    }

    public BigDecimal getExtended() {
        return extended;
    }

    public void setExtended(BigDecimal extended) {
        this.extended = extended;
    }

    public Vendor getIdVendor() {
        return idVendor;
    }

    public void setIdVendor(Vendor idVendor) {
        this.idVendor = idVendor;
    }

    public Integer getHasId() {
        return hasId;
    }

    public void setHasId(Integer hasId) {
        this.hasId = hasId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (hasId != null ? hasId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Has)) {
            return false;
        }
        Has other = (Has) object;
        if ((this.hasId == null && other.hasId != null) || (this.hasId != null && !this.hasId.equals(other.hasId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.Has[ hasId=" + hasId + " ]";
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

}
