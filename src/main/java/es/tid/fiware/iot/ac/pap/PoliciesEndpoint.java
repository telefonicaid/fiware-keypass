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
import es.tid.fiware.iot.ac.rs.Tenant;
import es.tid.fiware.iot.ac.rs.Correlator;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/pap/v1/subject/{subject}/policy/{policyId}")
@Produces(MediaType.APPLICATION_XML)
public class PoliciesEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoliciesEndpoint.class);

    private PolicyDao dao;

    public PoliciesEndpoint(PolicyDao dao) {
        this.dao = dao;
    }

    @GET
    @UnitOfWork
    public Response getPolicy(@Tenant String tenant,
                              @Correlator String correlator,
                              @PathParam("subject") String subject,
                              @PathParam("policyId") String policyId) {
        
        LOGGER.debug("Getting policy with id [{}] for [{}] and subject [{}]", policyId, tenant, subject);
        
        String id = URLEncoding.decode(policyId);

        Policy p = dao.loadPolicy(tenant, subject, id);
        if (p != null) {
            return Response.ok(p.getPolicy()).build();
        } else {
            return Response.status(404).build();
        }
    }

    @DELETE
    @UnitOfWork
    @Correlator
    public Response deletePolicy(@Tenant String tenant,
                                 @Correlator String correlator,
                                 @PathParam("subject") String subject,
                                 @PathParam("policyId") String policyId) {

        String id = URLEncoding.decode(policyId);
        LOGGER.debug("Removing policy with id [{}] for [{}] and subject [{}]", policyId, tenant, subject);

        Policy p = dao.loadPolicy(tenant, subject, id);
        if (p != null) {
            dao.deletePolicy(p);
            return Response.status(204).build();
        } else {
            return Response.status(404).build();
        }
    }

    @PUT
    @UnitOfWork
    @Correlator
    public Response updatePolicy(@Tenant String tenant,
                                 @Correlator String correlator,
                                 @PathParam("subject") String subject,
                                 @PathParam("policyId") String policyId,
                                 String policy) {
        
        LOGGER.debug("Updating policy with id [{}] for [{}] and subject [{}]", policyId, tenant, subject);

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
