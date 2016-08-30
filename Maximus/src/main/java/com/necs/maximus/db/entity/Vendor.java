/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.db.entity;

import java.io.Serializable;
import java.util.Collection;
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
 * @author luis
 */
@Entity
@Table(name = "vendor")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Vendor.findAll", query = "SELECT v FROM Vendor v"),
    @NamedQuery(name = "Vendor.findByIdVendor", query = "SELECT v FROM Vendor v WHERE v.idVendor = :idVendor"),
    @NamedQuery(name = "Vendor.findByCompanyName", query = "SELECT v FROM Vendor v WHERE v.companyName = :companyName"),
    @NamedQuery(name = "Vendor.findByAddress", query = "SELECT v FROM Vendor v WHERE v.address = :address"),
    @NamedQuery(name = "Vendor.findByCity", query = "SELECT v FROM Vendor v WHERE v.city = :city"),
    @NamedQuery(name = "Vendor.findByState", query = "SELECT v FROM Vendor v WHERE v.state = :state"),
    @NamedQuery(name = "Vendor.findByCountry", query = "SELECT v FROM Vendor v WHERE v.country = :country"),
    @NamedQuery(name = "Vendor.findByZip", query = "SELECT v FROM Vendor v WHERE v.zip = :zip"),
    @NamedQuery(name = "Vendor.findByFirstName", query = "SELECT v FROM Vendor v WHERE v.firstName = :firstName"),
    @NamedQuery(name = "Vendor.findByLastName", query = "SELECT v FROM Vendor v WHERE v.lastName = :lastName"),
    @NamedQuery(name = "Vendor.findByPhoneNumber", query = "SELECT v FROM Vendor v WHERE v.phoneNumber = :phoneNumber"),
    @NamedQuery(name = "Vendor.findByEmail", query = "SELECT v FROM Vendor v WHERE v.email = :email")})
public class Vendor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "id_vendor")
    private String idVendor;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "company_name")
    private String companyName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "address")
    private String address;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "city")
    private String city;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "state")
    private String state;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "country")
    private String country;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "zip")
    private String zip;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "first_name")
    private String firstName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "last_name")
    private String lastName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "phone_number")
    private String phoneNumber;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 45)
    @Column(name = "email")
    private String email;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idVendor")
    private Collection<Has> hasCollection;

    public Vendor() {
    }

    public Vendor(String idVendor) {
        this.idVendor = idVendor;
    }

    public Vendor(String idVendor, String companyName, String address, String city, String state, String country, String zip, String firstName, String lastName, String phoneNumber) {
        this.idVendor = idVendor;
        this.companyName = companyName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zip = zip;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getIdVendor() {
        return idVendor;
    }

    public void setIdVendor(String idVendor) {
        this.idVendor = idVendor;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    @XmlTransient
    public Collection<Has> getHasCollection() {
        return hasCollection;
    }

    public void setHasCollection(Collection<Has> hasCollection) {
        this.hasCollection = hasCollection;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idVendor != null ? idVendor.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Vendor)) {
            return false;
        }
        Vendor other = (Vendor) object;
        if ((this.idVendor == null && other.idVendor != null) || (this.idVendor != null && !this.idVendor.equals(other.idVendor))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.Vendor[ idVendor=" + idVendor + " ]";
    }

}
