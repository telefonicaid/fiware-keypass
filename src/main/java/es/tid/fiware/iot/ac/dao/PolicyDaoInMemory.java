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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PolicyDaoInMemory implements PolicyDao {

    private Map<String, Map<String, Policy>> db =
            new ConcurrentHashMap<String, Map<String, Policy>>();

    private AtomicInteger ids = new AtomicInteger(1);

    private Map<String, Policy> getDBList(String tenant, @Nullable String subject) {
        Map<String, Policy> l = db.get(tenant + ":" + subject);
        if (l == null) {
            l = new HashMap<String, Policy>();
            db.put(tenant + ":" + subject, l);
        }
        return l;
    }

    @Override
    public Policy create(String tenant, @Nullable String subject, String policy) {
        String id = "" + ids.getAndIncrement();
        Policy p = new Policy(id, tenant, subject, policy);
        getDBList(tenant, subject).put(id, p);
        return p;
    }

    @Override
    public Policy get(String tenant, @Nullable String subject, String id) {
        return getDBList(tenant, subject).get(id);
    }

    @Override
    public Collection<Policy> list(String tenant, @Nullable String subject) {
        return getDBList(tenant, subject).values();
    }

    @Override
    public Collection<Policy> listAll(String tenant) {
        String tenantKey = tenant + ":";
        Collection<Policy> policies = new ArrayList<Policy>();
        for (String key : db.keySet()) {
            if (key.startsWith(tenant+":")) {
                policies.addAll(db.get(key).values());
            }
        }
        return policies;
    }

    @Override
    public Policy update(Policy policy) {
        return getDBList(policy.getTenant(), policy.getSubject()).put(policy.getId(), policy);
    }

    @Override
    public Policy delete(Policy policy) {
        return getDBList(policy.getTenant(), policy.getSubject()).remove(policy.getId());
    }

}
