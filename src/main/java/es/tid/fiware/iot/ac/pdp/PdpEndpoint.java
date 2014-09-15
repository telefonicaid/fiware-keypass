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
import es.tid.fiware.iot.ac.xacml.PDPFactory;
import org.w3c.dom.Document;
import org.wso2.balana.PDP;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    public Response enforce(@PathParam("tenant") String tenant,
            String XacmlRequest) {

        Collection<Policy> policies = dao.listAll(tenant);

        List<Document> xmlPolicies = toDocument(policies);

        PDP pdp = pdpFactory.build(xmlPolicies);

        return Response.ok(pdp.evaluate(XacmlRequest)).build();
    }

    private List<Document> toDocument(Collection<Policy> ps) {
        List<Document> docs = new ArrayList<Document>();
        for (Policy p : ps) {
            docs.add(Xml.toXml(p.getPolicy()));
        }
        return docs;
    }
}
