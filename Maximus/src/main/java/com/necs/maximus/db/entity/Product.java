/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.necs.maximus.db.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Carlos Moh
 */
@Entity
@Table(name = "product")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p"),
    @NamedQuery(name = "Product.findByPartNumber", query = "SELECT p FROM Product p WHERE p.partNumber = :partNumber"),
    @NamedQuery(name = "Product.findByManufacture", query = "SELECT p FROM Product p WHERE p.manufacture = :manufacture"),
    @NamedQuery(name = "Product.findByDescription", query = "SELECT p FROM Product p WHERE p.description = :description"),
    @NamedQuery(name = "Product.findByType", query = "SELECT p FROM Product p WHERE p.type = :type"),
    @NamedQuery(name = "Product.findByNotes", query = "SELECT p FROM Product p WHERE p.notes = :notes"),
    @NamedQuery(name = "Product.findByRetailPrice", query = "SELECT p FROM Product p WHERE p.retailPrice = :retailPrice"),
    @NamedQuery(name = "Product.findByWholesalePrice", query = "SELECT p FROM Product p WHERE p.wholesalePrice = :wholesalePrice"),
    @NamedQuery(name = "Product.findByPrice", query = "SELECT p FROM Product p WHERE p.price = :price")})
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "part_number")
    private String partNumber;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "manufacture")
    private String manufacture;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "type")
    private String type;
    @Size(max = 100)
    @Column(name = "notes")
    private String notes;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "retail_price")
    private BigDecimal retailPrice;
    @Column(name = "wholesale_price")
    private BigDecimal wholesalePrice;
    @Column(name = "price")
    private BigDecimal price;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private List<IsSubstitute> isSubstituteList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private List<Has> hasList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partNumber")
    private List<ProductCost> productCostList;

    public Product() {
    }

    public Product(String partNumber) {
        this.partNumber = partNumber;
    }

    public Product(String partNumber, String manufacture, String description, String type) {
        this.partNumber = partNumber;
        this.manufacture = manufacture;
        this.description = description;
        this.type = type;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public BigDecimal getWholesalePrice() {
        return wholesalePrice;
    }

    public void setWholesalePrice(BigDecimal wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @XmlTransient
    public List<IsSubstitute> getIsSubstituteList() {
        return isSubstituteList;
    }

    public void setIsSubstituteList(List<IsSubstitute> isSubstituteList) {
        this.isSubstituteList = isSubstituteList;
    }

    @XmlTransient
    public List<Has> getHasList() {
        return hasList;
    }

    public void setHasList(List<Has> hasList) {
        this.hasList = hasList;
    }

    @XmlTransient
    public List<ProductCost> getProductCostList() {
        return productCostList;
    }

    public void setProductCostList(List<ProductCost> productCostList) {
        this.productCostList = productCostList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (partNumber != null ? partNumber.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Product)) {
            return false;
        }
        Product other = (Product) object;
        if ((this.partNumber == null && other.partNumber != null) || (this.partNumber != null && !this.partNumber.equals(other.partNumber))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.Product[ partNumber=" + partNumber + " ]";
    }

}
