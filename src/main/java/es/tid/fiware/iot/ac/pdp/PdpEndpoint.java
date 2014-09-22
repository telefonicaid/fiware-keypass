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
import es.tid.fiware.iot.ac.util.Xml;
import es.tid.fiware.iot.ac.xacml.Extractors;
import es.tid.fiware.iot.ac.xacml.PDPFactory;
import io.dropwizard.hibernate.UnitOfWork;
import org.w3c.dom.Document;
import org.wso2.balana.PDP;
import org.xml.sax.SAXException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Path("/pdp/v3/{tenant}")
@Produces(MediaType.APPLICATION_XML)
public class PdpEndpoint {

    private PolicyDao dao;
    private PDPFactory pdpFactory = new PDPFactory();

    public PdpEndpoint(PolicyDao dao) {
        this.dao = dao;
    }

    @POST
    @UnitOfWork
    public Response enforce(@PathParam("tenant") String tenant,
            String xacmlRequest) {

        Collection<Policy> policies = new ArrayList<Policy>();

        Collection<String> subjects;
        try {
            subjects = Extractors.extractSubjectIds(xacmlRequest);
        } catch (Exception e) {
            return Response.status(400).build();
        }

        for (String subject : subjects) {
            policies.addAll(dao.getPolicies(tenant, subject));
        }

        List<Document> xmlPolicies = null;
        try {
            xmlPolicies = toDocument(policies);
        } catch (Exception e) {
            // should not happen if policies are tested when provisioned.
            return Response.status(500).build();
        }

        PDP pdp = pdpFactory.build(xmlPolicies);

        return Response.ok(pdp.evaluate(xacmlRequest)).build();
    }

    private List<Document> toDocument(Collection<Policy> ps) throws IOException, SAXException {
        List<Document> docs = new ArrayList<Document>();
        for (Policy p : ps) {
            docs.add(Xml.toXml(p.getPolicy()));
        }
        return docs;
    }
}
