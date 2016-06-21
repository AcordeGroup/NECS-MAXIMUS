/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.necs.maximus.db.facade;

import com.necs.maximus.db.entity.QuoteStatus;
import java.util.Date;
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
public class QuoteStatusFacade extends AbstractFacade<QuoteStatus> {

    @PersistenceContext(unitName = "com.necs_Maximus_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public QuoteStatusFacade() {
        super(QuoteStatus.class);
    }
    
    public List<QuoteStatus> findQuoteByStatusActual(){
        
         Query query = em.createQuery("select q "
                + "from QuoteStatus q "
                + "where q.endDate = null ");
      
        return query.getResultList();
    
    }
    
}
