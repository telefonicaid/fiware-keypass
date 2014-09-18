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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/pap/v1/{tenant}/subject/{subject}/policy/{policyId}")
@Produces(MediaType.APPLICATION_XML)
public class PoliciesEndpoint {

    private PolicyDao dao;

    public PoliciesEndpoint(PolicyDao dao) {
        this.dao = dao;
    }

    @GET
    public Response getPolicy(@PathParam("tenant") String tenant,
            @PathParam("subject") String subject,
            @PathParam("policyId") String policyId) {

        String id = URLEncoding.decode(policyId);

        Policy p = dao.loadPolicy(tenant, subject, id);
        if (p != null) {
            return Response.ok(p.getPolicy()).build();
        } else {
            return Response.status(404).build();
        }
    }

    @DELETE
    public Response deletePolicy(@PathParam("tenant") String tenant,
            @PathParam("subject") String subject,
            @PathParam("policyId") String policyId) {

        String id = URLEncoding.decode(policyId);

        Policy p = dao.loadPolicy(tenant, subject, id);
        if (p != null) {
            dao.deletePolicy(p);
            return Response.ok(p.getPolicy()).build();
        } else {
            return Response.status(404).build();
        }
    }

    @PUT
    public Response updatePolicy(@PathParam("tenant") String tenant,
            @PathParam("subject") String subject,
            @PathParam("policyId") String policyId,
            String policy) {

        String id = URLEncoding.decode(policyId);

        Policy p = dao.loadPolicy(tenant, subject, id);
        if (p != null) {
            Policy newP = dao.updatePolicy(new Policy(id, tenant, subject, policy));
            return Response.ok(newP.getPolicy()).build();
        } else {
            return Response.status(404).build();
        }
    }
}
