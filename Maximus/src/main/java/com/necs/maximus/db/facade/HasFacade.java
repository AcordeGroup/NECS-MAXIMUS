/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.necs.maximus.db.facade;

import com.necs.maximus.db.entity.Has;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Carlos Moh
 */
@Stateless
public class HasFacade extends AbstractFacade<Has> {

    @PersistenceContext(unitName = "com.necs_Maximus_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HasFacade() {
        super(Has.class);
    }
    
     public List<Has> findHasByIdProduct(String idProduct) {

        Query query = em.createNamedQuery("Has.findByPartNumber");
        query.setParameter("partNumber", idProduct);
        return query.getResultList();

    }
     
     public List<Has> findHasByIdProductAndIdAgent(String idProduct, String idAgent) {

        Query query = em.createQuery("SELECT h from Has h WHERE h.product.partNumber = :idProduct "
                + "AND h.quote.idAgent.idAgent = :idAgent ");
        query.setParameter("idProduct", idProduct);
        query.setParameter("idAgent", idAgent);
        return query.getResultList();

    }

}
