/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.enums;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author luis
 */
public enum ContactType {

    RESELLER(1, "Reseller"),
    END_USER(2, "End user");

    private Integer id;
    private String name;

    private ContactType(Integer id, String name) {
        this.id = id;
        this.name = name;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<String> getListValues() {
        List<String> list = new ArrayList<>();

        for (ContactType value : ContactType.values()) {
            list.add(value.getName());
        }
        return list;
    }

}
