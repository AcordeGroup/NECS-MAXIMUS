/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.enums;

/**
 *
 * @author luis
 */
public enum ShippingCostType {

    NO(0, "No"),
    YES_PRICE_PER_ITEM(1, "Yes - in price per item"),
    YES_SEPARATE(2, "Yes - separate");

    private Integer idType;
    private String type;

    private ShippingCostType(Integer idType, String type) {

        this.idType = idType;
        this.type = type;

    }

    public Integer getIdType() {
        return idType;
    }

    public void setIdType(Integer idType) {
        this.idType = idType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static ShippingCostType getEnumByType(String name) {
        ShippingCostType type = null;
        for (ShippingCostType shitype : ShippingCostType.values()) {
            if (shitype.getType().equals(name)) {
                type = shitype;
            }
        }
        return type;
    }

    public static ShippingCostType getEnumByIdType(Integer id) {
        ShippingCostType type = null;
        for (ShippingCostType shitype : ShippingCostType.values()) {
            if (shitype.getIdType().equals(id)) {
                type = shitype;
            }
        }
        return type;
    }
}
