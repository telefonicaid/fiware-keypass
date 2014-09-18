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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Tenant Policy management.
 */
@Path("/pap/v1/{tenant}")
@Produces(MediaType.APPLICATION_XML)
public class TenantEndpoint {

    private final PolicyDao dao;

    public TenantEndpoint(PolicyDao dao) {
        this.dao = dao;
    }

    /**
     * Delete the Tenant (and all its subjects and policies).
     *
     * @param tenant
     * @return
     */
    @DELETE
    public Response delete(@PathParam("tenant") String tenant) {
        // TODO return policies from removed tenant?
        dao.deleteFromTenant(tenant);
        return Response.status(204).build();
    }


    /**
     * Returns all the Subjects.
     *
     * @param tenant
     * @return
     */
    @GET
    public Response getSubjects(@PathParam("tenant") String tenant) {
        // TODO Which format? XML? define it!
        return Response.status(501).build();
    }
}
