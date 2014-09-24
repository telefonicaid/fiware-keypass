package es.tid.fiware.iot.ac.pap;

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
import es.tid.fiware.iot.ac.model.PolicySet;
import es.tid.fiware.iot.ac.util.Xml;
import es.tid.fiware.iot.ac.xacml.PDPFactory;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Collection;

/**
 * Manages Policies with a Subject.
 */
@Path("/pap/v1/{tenant}/subject/{subject}")
@Produces(MediaType.APPLICATION_XML)
public class SubjectEndpoint {

    private PolicyDao dao;

    private PDPFactory factory = new PDPFactory();

    private static final Logger LOGGER = LoggerFactory.getLogger(SubjectEndpoint.class);

    public SubjectEndpoint(PolicyDao dao) {
        this.dao = dao;
    }

    @GET
    @UnitOfWork
    public Response getPolicies(@PathParam("tenant") String tenant,
            @PathParam("subject") String subject) {
        try {
            Collection<Policy> policyList = dao.getPolicies(tenant, subject);
            
            PolicySet ps = new PolicySet(tenant + ":" + subject, policyList);
            Document setDocument = ps.toXml();
            String result = Xml.toString(setDocument);
            return Response.ok(result).build();
        } catch (IOException ex) {
            return Response.status(500).build();
        } catch (SAXException ex) {
            return Response.status(500).build();
        } catch (TransformerException ex) {
            return Response.status(500).build();
        }
        
    }

    /**
     * Add a new Policy to this Subject.
     *
     * @param info
     * @param tenant
     * @param subject
     * @param policy XML Policy as String.
     * @return
     */
    @POST
    @UnitOfWork
    public Response createPolicy(@Context UriInfo info,
            @PathParam("tenant") String tenant,
            @PathParam("subject") String subject, String policy) {
        String id;
        try {
            id = factory.create(Xml.toXml(policy)).getId().toString();
        } catch (Exception e) {
            LOGGER.error("Cannot parse policy: " + e.getMessage());
            return Response.status(400).build();
        }
        dao.createPolicy(new Policy(id, tenant, subject, policy));
        return Response.created(info.getAbsolutePathBuilder().path("/policy/" + id).build()).build();
    }

    /**
     * Delete the Subject (and all its policies).
     */
    @DELETE
    @UnitOfWork
    public Response delete(@PathParam("tenant") String tenant,
            @PathParam("subject") String subject) {
        dao.deleteFromSubject(tenant, subject);
        return Response.status(204).build();
    }

}
