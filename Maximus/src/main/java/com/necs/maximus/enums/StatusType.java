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
public enum StatusType {

    OPEN(1, "OPEN"),
    IN_PROGRESS(2, "IN PROGRESS"),
    READY(3, "READY"),
    SENT(4, "SENT"),
    CLOSE(4, "CLOSE");

    private Integer id;
    private String name;

    private StatusType(Integer id, String name) {
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

    public static StatusType getStatusByName(String nameStatus) {
        StatusType status = null;
        for (StatusType type : StatusType.values()) {
            if (type.getName().equals(nameStatus)) {
                status = type;
            }
        }
        return status;
    }
}
