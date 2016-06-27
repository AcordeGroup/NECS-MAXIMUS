/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.necs.maximus.db.facade;

import com.necs.maximus.db.entity.Agent;
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
    
    public List<QuoteStatus> findQuoteStatusByStatusActual(String idAgent){
        
         Query query = em.createQuery("select q "
                + "from QuoteStatus q "
                + "where q.endDate = null "
                + "and q.idQuote.idAgent.idAgent = :idAgent ");
         query.setParameter("idAgent", idAgent);
        return query.getResultList();
    
    }
    
    public List<QuoteStatus> findQuoteStatusByStatusAndAgent(String status, String idAgent){
        
         Query query = em.createQuery("select q "
                + "from QuoteStatus q "
                + "where q.status = :status "
                + "and q.idQuote.idAgent.idAgent = :idAgent ");
         
         query.setParameter("status", status);
         query.setParameter("idAgent", idAgent);
      
        return query.getResultList();
    
    }
    
    public List<QuoteStatus> findQuoteStatusByIdAgent(Agent agent){
        
         Query query = em.createQuery("select q "
                + "from QuoteStatus q "
                + "JOIN q.idQuote.manageList qa "
                + "where qa.agent = :agent ");
        
         query.setParameter("agent", agent);
      
        return query.getResultList();
    
    }
    
    public List<QuoteStatus> findQuoteStatusByStatus(List<String> status){
        
         Query query = em.createQuery("select q "
                + "from QuoteStatus q "
                + "where q.endDate = null "
                + "and q.status in :status ");
         
         query.setParameter("status", status);
      
        return query.getResultList();
    
    }
    
    public List<QuoteStatus> findAllQuoteStatusActual(){
        
         Query query = em.createQuery("select q "
                + "from QuoteStatus q "
                + "where q.endDate = null ");
        return query.getResultList();
    
    }
    
}
