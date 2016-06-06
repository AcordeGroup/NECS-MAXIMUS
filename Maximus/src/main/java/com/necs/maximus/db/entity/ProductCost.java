/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.necs.maximus.db.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "product_cost")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProductCost.findAll", query = "SELECT p FROM ProductCost p"),
    @NamedQuery(name = "ProductCost.findByIdProductCost", query = "SELECT p FROM ProductCost p WHERE p.idProductCost = :idProductCost"),
    @NamedQuery(name = "ProductCost.findByCost", query = "SELECT p FROM ProductCost p WHERE p.cost = :cost"),
    @NamedQuery(name = "ProductCost.findByInitDate", query = "SELECT p FROM ProductCost p WHERE p.initDate = :initDate"),
    @NamedQuery(name = "ProductCost.findByEndDate", query = "SELECT p FROM ProductCost p WHERE p.endDate = :endDate")})
public class ProductCost implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_product_cost")
    private Integer idProductCost;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "cost")
    private BigDecimal cost;
    @Basic(optional = false)
    @NotNull
    @Column(name = "init_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date initDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.TIME)
    private Date endDate;
    @JoinColumn(name = "part_number", referencedColumnName = "part_number")
    @ManyToOne(optional = false)
    private Product partNumber;

    public ProductCost() {
    }

    public ProductCost(Integer idProductCost) {
        this.idProductCost = idProductCost;
    }

    public ProductCost(Integer idProductCost, BigDecimal cost, Date initDate) {
        this.idProductCost = idProductCost;
        this.cost = cost;
        this.initDate = initDate;
    }

    public Integer getIdProductCost() {
        return idProductCost;
    }

    public void setIdProductCost(Integer idProductCost) {
        this.idProductCost = idProductCost;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
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

    public Product getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(Product partNumber) {
        this.partNumber = partNumber;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProductCost != null ? idProductCost.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProductCost)) {
            return false;
        }
        ProductCost other = (ProductCost) object;
        if ((this.idProductCost == null && other.idProductCost != null) || (this.idProductCost != null && !this.idProductCost.equals(other.idProductCost))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.ProductCost[ idProductCost=" + idProductCost + " ]";
    }

}
