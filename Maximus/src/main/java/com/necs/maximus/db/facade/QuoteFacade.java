/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.db.facade;

import com.necs.maximus.db.entity.Quote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Carlos Moh
 */
@Stateless
public class QuoteFacade extends AbstractFacade<Quote> {

    @PersistenceContext(unitName = "com.necs_Maximus_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public QuoteFacade() {
        super(Quote.class);
    }

    public Quote findQuoteByIdQuoteAndStatusActual(Integer idQuote) {

            Query query = em.createQuery("select q "
                + "from Quote q "
                + "JOIN q.quoteStatusList ql "
                + "where ql.endDate = null "
                + "and q.idQuote = :idQuote ");
        query.setParameter("idQuote", idQuote);

        return (Quote) query.getSingleResult();

    }

}
