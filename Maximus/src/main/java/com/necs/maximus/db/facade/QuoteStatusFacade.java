/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.necs.maximus.db.facade;

import com.necs.maximus.db.entity.Quote;
import com.necs.maximus.db.entity.QuoteStatus;
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
    
   public QuoteStatus findQuoteStatusByIdQuote(Quote idQuote) {
        Query query = em.createQuery("select q "
                + "from QuoteStatus q "
                + "where q.idQuote = :idQuote "
                + "and q.endDate is null");
        query.setParameter("idQuote", idQuote);
        return (QuoteStatus) query.getSingleResult();

    }
    
}
