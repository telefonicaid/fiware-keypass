package es.tid.fiware.iot.ac.pdp;

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

import com.codahale.metrics.annotation.Timed;
import es.tid.fiware.iot.ac.xacml.Extractors;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.IOException;
import org.wso2.balana.PDP;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;
import javax.xml.xpath.XPathExpressionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

@Path("/pdp/v3/{tenant}")
@Produces(MediaType.APPLICATION_XML)
public class PdpEndpoint {

    private final PdpFactory pdpFactory;
    private static final Logger LOGGER = LoggerFactory.getLogger(PdpEndpoint.class);

    public PdpEndpoint(PdpFactory pdpFactory) {
        this.pdpFactory = pdpFactory;
    }

    @POST
    @UnitOfWork
    @Timed
    public Response enforce(@PathParam("tenant") String tenant,
            String xacmlRequest) {

        LOGGER.debug("Enforcing policies for tenant [{}]", tenant);

        PDP pdp = pdpFactory.get(tenant, extractSubjectIds(xacmlRequest));
        return Response.ok(pdp.evaluate(xacmlRequest)).build();
    }

    private Set<String> extractSubjectIds(String xacmlRequest)
            throws WebApplicationException {
        try {
            return new HashSet(Extractors.extractSubjectIds(xacmlRequest));
        } catch (XPathExpressionException | IOException | SAXException e) {
            throw new WebApplicationException(400);
        }
    }

}
