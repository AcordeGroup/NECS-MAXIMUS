/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.necs.maximus.db.facade;

import com.necs.maximus.db.entity.Manage;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Carlos Moh
 */
@Stateless
public class ManageFacade extends AbstractFacade<Manage> {

    @PersistenceContext(unitName = "com.necs_Maximus_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ManageFacade() {
        super(Manage.class);
    }

}
