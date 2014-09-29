package es.tid.fiware.iot.ac.pdp;

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

import es.tid.fiware.iot.ac.dao.PolicyDao;
import es.tid.fiware.iot.ac.model.Policy;
import es.tid.fiware.iot.ac.util.CacheFactory;
import es.tid.fiware.iot.ac.util.Xml;
import es.tid.fiware.iot.ac.xacml.PDPFactory;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.w3c.dom.Document;
import org.wso2.balana.PDP;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdpFactoryCached implements PdpFactory {

    private final PolicyDao dao;

    private final CacheFactory cacheFactory;

    private final PDPFactory pdpFactory = new PDPFactory();

    private static final Logger LOGGER = LoggerFactory.getLogger(PdpFactoryCached.class);

    public PdpFactoryCached(PolicyDao dao, CacheFactory cacheFactory) {
        this.dao = dao;
        this.cacheFactory = cacheFactory;
    }

    @Override
    public PDP get(String tenant, Set<String> subjects) {
        Ehcache cache = cacheFactory.get(tenant);

        Element el = cache.get(subjects);

        if (el != null) {
            return (PDP) el.getObjectValue();
        } else {
            Collection<Policy> policies = toPolicy(tenant, subjects);
            List<Document> xmlPolicies = toDocument(policies);
            PDP pdp = pdpFactory.build(xmlPolicies);
            cache.put(new Element(subjects, pdp));
            return pdp;
        }
    }

    private Collection<Policy> toPolicy(String tenant, Set<String> subjects) {
        Collection<Policy> policies = new ArrayList<Policy>();
        for (String subject : subjects) {
            LOGGER.debug("Getting policies for subject [{}]", subject);
               
            policies.addAll(dao.getPolicies(tenant, subject));
        }
        return policies;
    }

    private List<Document> toDocument(Collection<Policy> ps) {
        List<Document> docs = new ArrayList<Document>();
        for (Policy p : ps) {
            try {
                LOGGER.trace("Policy [{}] for subject [{}]: {}", p.getId(), p.getSubject(), p.getPolicy());
                docs.add(Xml.toXml(p.getPolicy()));
            } catch (IOException | SAXException e) {
                throw new RuntimeException(e);
            }
        }
        return docs;
    }
}
