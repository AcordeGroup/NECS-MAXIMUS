/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.db.facade;

import com.necs.maximus.db.entity.IsSubstitute;
import com.necs.maximus.db.entity.Product;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Carlos Moh
 */
@Stateless
public class IsSubstituteFacade extends AbstractFacade<IsSubstitute> {

    @PersistenceContext(unitName = "com.necs_Maximus_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IsSubstituteFacade() {
        super(IsSubstitute.class);
    }

    public IsSubstitute findIsSubstituteByProductBaseAndSubstitute(Product base, Product substitute) {

        try {

            Query query = em.createQuery("select i "
                    + "from IsSubstitute i "
                    + "where i.partNumberBase = :base "
                    + " and i.partNumberSubstitute = :substitute ");
            query.setParameter("base", base);
            query.setParameter("substitute", substitute);

            return (IsSubstitute) query.getSingleResult();

        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
