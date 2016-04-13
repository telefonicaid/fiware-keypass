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
import es.tid.fiware.iot.ac.rs.Tenant;
import es.tid.fiware.iot.ac.rs.Correlator;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tenant Policy management.
 */
@Path("/pap/v1")
@Produces(MediaType.APPLICATION_XML)
public class TenantEndpoint {

    private final PolicyDao dao;
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantEndpoint.class);

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
    @UnitOfWork
    public Response delete(@Tenant String tenant,
                           @Correlator String correlator) {
        LOGGER.debug("Deleting all the resources for [{}]", tenant);

        dao.deleteFromTenant(tenant);
        return Response.status(204).build();
    }

}
