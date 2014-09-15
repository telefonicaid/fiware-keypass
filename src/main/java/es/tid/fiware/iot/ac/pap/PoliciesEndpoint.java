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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/pap/v1/{tenant}/{subject}")
@Produces(MediaType.APPLICATION_XML)
public class PoliciesEndpoint {

    private PolicyDao dao;

    public PoliciesEndpoint(PolicyDao dao) {
        this.dao = dao;
    }

    @GET
    public Response getPolicies(@DefaultValue("20") @QueryParam("size") int size,
            @DefaultValue("0") @QueryParam("offset") int offset,
            @PathParam("tenant") String tenant,
            @PathParam("subject") String subject) {
        // TODO
        return Response.status(501).build();
    }

    @POST
    public Response createPolicy(@Context UriInfo info,
             @PathParam("tenant") String tenant,
             @PathParam("subject") String subject, String policy) {
        Policy p = dao.create(tenant, subject, policy);
        return Response.created(info.getAbsolutePathBuilder().path(p.getId()).build()).build();
    }

    @GET
    @Path("{id}")
    public Response getPolicy(@DefaultValue("20") @QueryParam("size") int size,
            @DefaultValue("0") @QueryParam("offset") int offset,
            @PathParam("tenant") String tenant,
            @PathParam("subject") String subject,
            @PathParam("id") String id) {

        Policy p = dao.get(tenant, subject, id);
        if (p != null) {
            return Response.ok(p.getPolicy()).build();
        } else {
            return Response.status(404).build();
        }
    }


}
