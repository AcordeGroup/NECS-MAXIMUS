/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.enums;

/**
 *
 * @author Carlos Moh
 */
public enum AgentType {

    Administrator("Administrator"),
    Sales("Sales"),
    Purchasing("Purchasing");

    String type;

    private AgentType(String type) {
        this.type = type;

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
