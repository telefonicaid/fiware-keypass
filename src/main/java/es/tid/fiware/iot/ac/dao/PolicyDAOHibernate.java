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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyDAOHibernate extends AbstractDAO<Policy> implements PolicyDao  {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyDAOHibernate.class);

    public PolicyDAOHibernate(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Policy createPolicy(Policy policy) {
        LOGGER.debug("Persisting policy [{}] for subject [{}]", policy.getId(), policy.getSubject());
        LOGGER.trace("Policy contents: {}", policy.getPolicy());
        return persist(policy);
    }

    @Override
    public Policy loadPolicy(String tenant, String subject, String id) {
        LOGGER.debug("Getting policy for tenant [{}] for subject [{}] and id [{}]", tenant, subject, id);
        
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
        LOGGER.debug("Updating policy [{}] for subject [{}]", policy.getId(), policy.getSubject());
        LOGGER.trace("Policy contents: {}", policy.getPolicy());

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
        LOGGER.debug("Deleting policy [{}] for subject [{}]", policy.getId(), policy.getSubject());
        LOGGER.trace("Policy contents: {}", policy.getPolicy());
        
        this.currentSession().delete(policy);
        
        return policy;
    }

    @Override
    public void deleteFromTenant(String tenant) {
        LOGGER.debug("Deleting all the content from tenant [{}]", tenant);
        String hql = "DELETE FROM Policy "  + 
             "WHERE internalId.tenant = :tenant_id";
        
        Query query = this.currentSession().createQuery(hql);
        query.setParameter("tenant_id", tenant);
        int result = query.executeUpdate();
    }

    @Override
    public void deleteFromSubject(String tenant, String subject) {
        LOGGER.debug("Deleting all the content from tenant [{}]"
                + " and subject [{}]", tenant, subject);
        String hql = "DELETE FROM Policy "  + 
             "WHERE subject = :subject_id";
        
        Query query = this.currentSession().createQuery(hql);
        query.setParameter("subject_id", subject);
        int result = query.executeUpdate();
    }
    
}
