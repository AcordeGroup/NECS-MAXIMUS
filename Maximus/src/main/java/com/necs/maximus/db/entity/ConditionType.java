/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.necs.maximus.db.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
 * @author Luis Castañeda <luis.castaneda at acorde.com.ve>
 */
@Entity
@Table(name = "condition_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ConditionType.findAll", query = "SELECT c FROM ConditionType c"),
    @NamedQuery(name = "ConditionType.findByIdConditionType", query = "SELECT c FROM ConditionType c WHERE c.idConditionType = :idConditionType"),
    @NamedQuery(name = "ConditionType.findByName", query = "SELECT c FROM ConditionType c WHERE c.name = :name"),
    @NamedQuery(name = "ConditionType.findByDescription", query = "SELECT c FROM ConditionType c WHERE c.description = :description")})
public class ConditionType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_condition_type")
    private Integer idConditionType;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "conditionType")
    private List<Has> hasList;

    public ConditionType() {
    }

    public ConditionType(Integer idConditionType) {
        this.idConditionType = idConditionType;
    }

    public ConditionType(Integer idConditionType, String name, String description) {
        this.idConditionType = idConditionType;
        this.name = name;
        this.description = description;
    }

    public Integer getIdConditionType() {
        return idConditionType;
    }

    public void setIdConditionType(Integer idConditionType) {
        this.idConditionType = idConditionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<Has> getHasList() {
        return hasList;
    }

    public void setHasList(List<Has> hasList) {
        this.hasList = hasList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idConditionType != null ? idConditionType.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConditionType)) {
            return false;
        }
        ConditionType other = (ConditionType) object;
        if ((this.idConditionType == null && other.idConditionType != null) || (this.idConditionType != null && !this.idConditionType.equals(other.idConditionType))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.necs.maximus.db.entity.ConditionType[ idConditionType=" + idConditionType + " ]";
    }

}
