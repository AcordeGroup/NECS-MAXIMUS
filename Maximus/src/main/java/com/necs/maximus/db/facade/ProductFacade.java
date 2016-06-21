/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.db.facade;

import com.necs.maximus.db.entity.Product;
import java.util.HashMap;
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
public class ProductFacade extends AbstractFacade<Product> {

    @PersistenceContext(unitName = "com.necs_Maximus_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductFacade() {
        super(Product.class);
    }

    public List<Product> findAllByFilter(HashMap<String, String> filters) {

        StringBuilder query_ = new StringBuilder();
        query_.append("WHERE 1 = 1 ");

        query_.append((filters.containsKey("partNumber")) ? "AND UPPER(p.partNumber) like :partNumber " : "");
        query_.append((filters.containsKey("manufacture")) ? "AND UPPER(p.manufacture) like :manufacture " : "");
        query_.append((filters.containsKey("description")) ? "AND UPPER(p.description) like :description " : "");
        query_.append((filters.containsKey("type")) ? "AND UPPER(p.type) like :type " : "");

        String queryString = "SELECT DISTINCT p "
                + "FROM Product p "
                + query_;

        Query query = em.createQuery(queryString);

        if (filters.containsKey("partNumber")) {
            query.setParameter("partNumber", "%" + filters.get("partNumber").toUpperCase() + "%");
        }

        if (filters.containsKey("manufacture")) {
            query.setParameter("manufacture", "%" + filters.get("manufacture").toUpperCase() + "%");
        }

        if (filters.containsKey("description")) {
            query.setParameter("description", "%" + filters.get("description").toUpperCase() + "%");
        }

        if (filters.containsKey("type")) {
            query.setParameter("type", "%" + filters.get("type").toUpperCase() + "%");
        }

        return query.getResultList();
    }

}
