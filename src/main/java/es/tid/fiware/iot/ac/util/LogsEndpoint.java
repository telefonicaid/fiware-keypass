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


@Path("/admin/log")
@Produces(MediaType.APPLICATION_XML)
public class LogsEndpoint {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(LogsEndpoint.class);

    private static String[] ValidLogLevels = {
        "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL"
    };

    public LogsEndpoint() {

    }

    /**
     * Change LogLevel
     *
     * @param logLevel
     * @return
     */

    @PUT
    @UnitOfWork
    public Response updateLogLevel(@Tenant String tenant,
                                   @Correlator String correlator,
                                   @QueryParam("logLevel") String logLevel
                                   ) {

        // Check logLevel proposed
        if (logLevel != null) {
            if (Arrays.asList(ValidLogLevels).contains(logLevel.toUpperCase())) {
                String newLogLevel = logLevel.toUpperCase();
                LOGGER.debug("trying to change log level changed to " + newLogLevel);
                LOGGER.setLevel(Level.toLevel(newLogLevel));
                LOGGER.info("Keypass log level changed to " + newLogLevel);
                return Response.status(200).build();
            } else {
                LOGGER.info("invalid log level " + logLevel);
                return Response.status(400).entity("invalid log level").build();
            }
        } else {
            LOGGER.info("log level missing " + logLevel);
            return Response.status(400).entity("log level missing").build();
        }

    }

}
