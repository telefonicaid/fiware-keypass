package es.tid.fiware.iot.ac.dao;

/*
 * Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
