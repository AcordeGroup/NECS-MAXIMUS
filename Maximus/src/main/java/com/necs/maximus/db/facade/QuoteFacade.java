/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.db.facade;

import com.necs.maximus.db.entity.Agent;
import com.necs.maximus.db.entity.Manage;
import com.necs.maximus.db.entity.Quote;
import com.necs.maximus.db.entity.QuoteStatus;
import com.necs.maximus.enums.AgentType;
import com.necs.maximus.enums.StatusType;
import java.util.ArrayList;
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

        Query query = em.createQuery("select distinct q "
                + "from Quote q "
                + "JOIN fetch q.quoteStatusList ql "
                + "where ql.endDate is null "
                + "and q.idQuote = :idQuote ");
        query.setParameter("idQuote", idQuote);

        return processQuote((Quote) query.getSingleResult());

    }

    public List<Quote> findQuoteByIdAgent(String idAgent) {

        Query query = em.createQuery("select distinct q "
                + "from Quote q "
                + "JOIN fetch q.quoteStatusList ql "
                + "where ql.endDate is null "
                + "and q.idAgent.idAgent = :idAgent ");
        query.setParameter("idAgent", idAgent);
        return processList(query.getResultList());

    }

    public List<Quote> findQuoteByStatusAndAgent(String status, Agent idAgent) {
        StringBuilder queryAlter = new StringBuilder();

        if (idAgent != null && idAgent.getType().equals(AgentType.Sales.getType())) {
            queryAlter.append("where q.idAgent = :idAgent ");
        } else {
            queryAlter.append("JOIN fetch q.manageList ml where ml.idAgent = :idAgent and ml.deallocationDate = null ");
        }
        if (status.equals(StatusType.CLOSE.getName())) {
            queryAlter.append("and ql.endDate is not null ");
        } else {
            queryAlter.append("and ql.endDate is null ");
        }

        Query query = em.createQuery("select distinct q "
                + "from Quote q "
                + "JOIN fetch q.quoteStatusList ql "
                + queryAlter
                + "and ql.status = :status ");

        query.setParameter("status", status);
        query.setParameter("idAgent", idAgent);

        if (status.equals(StatusType.CLOSE.getName())) {
            return query.getResultList();
        } else {
            return processList(query.getResultList());
        }
    }

    public List<Quote> findQuoteByIdAgent(Agent agent) {
        Query query = em.createQuery("select distinct q "
                + "from Quote q "
                + "JOIN fetch q.quoteStatusList qs "
                + "JOIN fetch q.manageList qa "
                + "where qa.idAgent = :idAgent "
                + "and qa.deallocationDate is null "
                + "and qs.endDate is null ");

        query.setParameter("idAgent", agent);

        return processList(query.getResultList());

    }

    public List<Quote> findQuoteByListStatus(List<String> status) {

        Query query = em.createQuery("select distinct q "
                + "from Quote q "
                + "JOIN fetch q.quoteStatusList ql "
                + "where ql.endDate is null "
                + "and ql.status in :status ");

        query.setParameter("status", status);

        return processList(query.getResultList());

    }

    public List<Quote> findAllQuoteByStatus(String status) {
        StringBuilder queryAlter = new StringBuilder();
        queryAlter.append("select distinct q from Quote q ").
                append("JOIN fetch q.quoteStatusList ql ").
                append("where ql.status = :status ");

        if (status.equals(StatusType.CLOSE.getName())) {
            queryAlter.append("and ql.endDate is not null ");
        } else {
            queryAlter.append("and ql.endDate is null ");
        }
        Query query = em.createQuery(queryAlter.toString());
        query.setParameter("status", status);
        
        if (status.equals(StatusType.CLOSE.getName())) {
            return query.getResultList();
        } else {
            return processList(query.getResultList());
        }

    }

    private List<Quote> processList(List<Quote> list) {
        List<QuoteStatus> auxListStatus = new ArrayList<>();
        List<Manage> auxListManage = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (Quote q : list) {

                if (q.getQuoteStatusList() != null && !q.getQuoteStatusList().isEmpty()) {
                    auxListStatus.addAll(q.getQuoteStatusList());
                    for (QuoteStatus qs : auxListStatus) {
                        if (qs.getEndDate() != null && !qs.getEndDate().equals("")) {
                            q.getQuoteStatusList().remove(qs);
                        }
                    }
                }
                if (q.getManageList() != null && !q.getManageList().isEmpty()) {
                    auxListManage.addAll(q.getManageList());
                    for (Manage ma : auxListManage) {
                        if (ma.getDeallocationDate() != null && !ma.getDeallocationDate().equals("")) {
                            q.getManageList().remove(ma);
                        }
                    }
                }
            }
        }
        return list;
    }

    private Quote processQuote(Quote quote) {
        List<QuoteStatus> auxListStatus = new ArrayList<>();
        List<Manage> auxListManage = new ArrayList<>();
        if (quote != null) {
            if (quote.getQuoteStatusList() != null && !quote.getQuoteStatusList().isEmpty()) {
                auxListStatus.addAll(quote.getQuoteStatusList());
                for (QuoteStatus qs : auxListStatus) {
                    if (qs.getEndDate() != null && !qs.getEndDate().equals("")) {
                        quote.getQuoteStatusList().remove(qs);
                    }
                }
            }
            if (quote.getManageList() != null && !quote.getManageList().isEmpty()) {
                auxListManage.addAll(quote.getManageList());
                for (Manage ma : auxListManage) {
                    if (ma.getDeallocationDate() != null && !ma.getDeallocationDate().equals("")) {
                        quote.getManageList().remove(ma);
                    }
                }
            }

        }
        return quote;
    }

}
