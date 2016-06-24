/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.db.facade;

import com.necs.maximus.db.entity.Quote;
import com.necs.maximus.db.entity.QuoteNote;
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
public class QuoteNoteFacade extends AbstractFacade<QuoteNote> {

    @PersistenceContext(unitName = "com.necs_Maximus_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public QuoteNoteFacade() {
        super(QuoteNote.class);
    }

    public List<QuoteNote> findQuoteNoteByIdQuote(Quote idQuote) {
        Query query = em.createQuery("select q "
                + "from QuoteNote q "
                + "where q.idQuote = :idQuote "
                + "order by q.idQuoteNote desc");
        query.setParameter("idQuote", idQuote);
        return query.getResultList();

    }

}
