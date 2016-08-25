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

    public static List<String> getListValues() {
        List<String> list = new ArrayList<>();

        for (AgentType value : AgentType.values()) {
            list.add(value.getType());
        }
        return list;
    }

    public static AgentType getAgentByType(String typeAgent) {
        AgentType typeA = null;
        for (AgentType type : AgentType.values()) {
            if (type.getType().equals(typeAgent)) {
                typeA = type;
            }
        }
        return typeA;
    }

}
