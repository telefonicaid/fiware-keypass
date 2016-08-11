package es.tid.fiware.iot.ac.util;

/*
 * Copyright 2016 Telefonica Investigación y Desarrollo, S.A.U
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

import es.tid.fiware.iot.ac.rs.Tenant;
import es.tid.fiware.iot.ac.rs.Correlator;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.IOException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Arrays;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.Level;

@Path("/version")
@Produces(MediaType.APPLICATION_XML)
public class VersionEndpoint {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(VersionEndpoint.class);

    public VersionEndpoint() {

    }


    /**
     * Get Version
     *
     * @return version
     */

    @GET
    @UnitOfWork
    public Response getVersion(@Tenant String tenant,
                               @Correlator String correlator
                               ) {
        // Get current version
        Logger root_LOGGER = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        String version = getClass().getPackage().getImplementationVersion();
        LOGGER.debug("get current version: " + version);
        return Response.status(200).entity(version).build();
    }


}
