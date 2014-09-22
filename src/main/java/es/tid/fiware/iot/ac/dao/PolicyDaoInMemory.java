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
