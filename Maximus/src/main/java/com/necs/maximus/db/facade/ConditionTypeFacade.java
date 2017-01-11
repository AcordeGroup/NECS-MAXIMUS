/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.necs.maximus.db.facade;

import com.necs.maximus.db.entity.ConditionType;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Luis Castañeda <luis.castaneda at acorde.com.ve>
 */
@Stateless
public class ConditionTypeFacade extends AbstractFacade<ConditionType>{
    
    @PersistenceContext(unitName =  "com.necs_Maximus_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public ConditionTypeFacade() {
        super(ConditionType.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    

}
