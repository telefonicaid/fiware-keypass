package es.tid.fiware.iot.ac.dao;/*
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A InMemory Impl, quick&dirty: not thread safe, no referential integrity.
 */
public class PolicyDaoInMemory implements PolicyDao {

    private Map<String, Map<String, Map<String, Policy>>> db =
            new ConcurrentHashMap<String, Map<String, Map<String, Policy>>>();

    private AtomicInteger ids = new AtomicInteger(1);

    private Map<String, Policy> get(String tenant, String subject) {
        Map<String, Map<String, Policy>> tenantEntry = db.get(tenant);
        if (tenantEntry == null) {
            tenantEntry = new ConcurrentHashMap<String, Map<String, Policy>>();
            db.put(tenant, tenantEntry);
        }
        Map<String, Policy> subjectEntry = tenantEntry.get(subject);
        if (subjectEntry == null) {
            subjectEntry = new ConcurrentHashMap<String, Policy>();
            tenantEntry.put(subject, subjectEntry);
        }
        return subjectEntry;
    }

    @Override
    public Policy createPolicy(Policy p) {
        get(p.getTenant(), p.getSubject()).put(p.getId(), p);
        return p;
    }

    @Override
    public Policy loadPolicy(String tenant, String subject, String id) {
        return get(tenant, subject).get(id);
    }

    @Override
    public Collection<Policy> getPolicies(String tenant, String subject) {
        return get(tenant, subject).values();
    }

    @Override
    public Collection<String> getSubjects(String tenant) {
        Map<String, Map<String, Policy>> subjects = db.get(tenant);
        if (subjects != null) {
            return subjects.keySet();
        } else {
            return Collections.EMPTY_SET;
        }
    }

    @Override
    public Policy updatePolicy(Policy policy) {
        return createPolicy(policy);
    }

    @Override
    public Policy deletePolicy(Policy p) {
        return get(p.getTenant(), p.getSubject()).remove(p.getId());
    }

    @Override
    public void deleteFromTenant(String tenant) {
        db.remove(tenant);
    }

    @Override
    public void deleteFromSubject(String tenant, String subject) {
        Map<String, Map<String, Policy>> subjects = db.get(tenant);
        if (subjects != null) {
            subjects.remove(subject);
        }
    }

}
