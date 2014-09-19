package es.tid.fiware.iot.ac.pap;
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

import es.tid.fiware.iot.ac.dao.PolicyDao;
import es.tid.fiware.iot.ac.model.Policy;
import es.tid.fiware.iot.ac.model.PolicySet;
import es.tid.fiware.iot.ac.util.Xml;
import es.tid.fiware.iot.ac.xacml.Extractors;
import java.io.IOException;
import java.util.Collection;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Manages Policies with a Subject.
 */
@Path("/pap/v1/{tenant}/subject/{subject}")
@Produces(MediaType.APPLICATION_XML)
public class SubjectEndpoint {

    private PolicyDao dao;

    private AtomicInteger idx = new AtomicInteger();

    public SubjectEndpoint(PolicyDao dao) {
        this.dao = dao;
    }

    @GET
    public Response getPolicies(@PathParam("tenant") String tenant,
            @PathParam("subject") String subject) {
        try {
            Collection<Policy> policyList = dao.getPolicies(tenant, subject);
            
            PolicySet ps = new PolicySet(subject + ":" + tenant, policyList);
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
    public Response createPolicy(@Context UriInfo info,
            @PathParam("tenant") String tenant,
            @PathParam("subject") String subject, String policy) {
        // TODO verify policy XML format
        // DAO will check uniqueness of policyId within tenant.
        String policyId;
        try {
            policyId = Extractors.extractPolicyId(policy);
        } catch (Exception e) {
            return Response.status(400).build();
        }
        dao.createPolicy(new Policy(policyId, tenant, subject, policy));
        String id = URLEncoding.encode(policyId);
        return Response.created(info.getAbsolutePathBuilder().path("/policy/" + id).build()).build();
    }

    /**
     * Delete the Subject (and all its policies).
     */
    @DELETE
    public Response delete(@PathParam("tenant") String tenant,
            @PathParam("subject") String subject) {
        dao.deleteFromSubject(tenant, subject);
        return Response.status(204).build();
    }

}
