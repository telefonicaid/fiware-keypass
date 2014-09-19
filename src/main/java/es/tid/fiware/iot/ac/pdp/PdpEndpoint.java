package es.tid.fiware.iot.ac.pdp;
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
