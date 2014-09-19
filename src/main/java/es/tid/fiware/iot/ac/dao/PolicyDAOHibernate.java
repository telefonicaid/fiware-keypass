package es.tid.fiware.iot.ac.dao;

/*
 * Telefónica Digital - Product Development and Innovation
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Copyright (c) Telefónica Investigación y Desarrollo S.A.U.
 * All rights reserved.
 */

import es.tid.fiware.iot.ac.model.Policy;
import io.dropwizard.hibernate.AbstractDAO;
import java.util.Collection;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

public class PolicyDAOHibernate extends AbstractDAO<Policy> implements PolicyDao  {

    public PolicyDAOHibernate(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Policy createPolicy(Policy policy) {
        return persist(policy);
    }

    @Override
    public Policy loadPolicy(String tenant, String subject, String id) {
        return get(new Policy.PolicyId(tenant, id));
    }

    @Override
    public Collection<Policy> getPolicies(String tenant, String subject) {        
        Criteria ct = this.currentSession().createCriteria(Policy.class)
            .add( Restrictions.eq("internalId.tenant", tenant) )
            .add( Restrictions.eq("subject", subject));
        return list(ct);
    }

    @Override
    public Collection<String> getSubjects(String tenant) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Policy updatePolicy(Policy policy) {
        String hql = "UPDATE Policy SET policy = :policy "  + 
             "WHERE tenant = :tenant and internalId.id = :id";
        
        Query query = this.currentSession().createQuery(hql);
        query.setParameter("policy", policy.getPolicy());
        query.setParameter("tenant", policy.getInternalId().getTenant());
        query.setParameter("id", policy.getInternalId().getId());
        int result = query.executeUpdate();

        return policy;
    }

    @Override
    public Policy deletePolicy(Policy policy) {
        this.currentSession().delete(policy);
        
        return policy;
    }

    @Override
    public void deleteFromTenant(String tenant) {
        String hql = "DELETE FROM Policy "  + 
             "WHERE internalId.tenant = :tenant_id";
        
        Query query = this.currentSession().createQuery(hql);
        query.setParameter("tenant_id", tenant);
        int result = query.executeUpdate();
    }

    @Override
    public void deleteFromSubject(String tenant, String subject) {
        String hql = "DELETE FROM Policy "  + 
             "WHERE subject = :subject_id";
        
        Query query = this.currentSession().createQuery(hql);
        query.setParameter("subject_id", subject);
        int result = query.executeUpdate();
    }
    
}
