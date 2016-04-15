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
import es.tid.fiware.iot.ac.rs.Tenant;
import es.tid.fiware.iot.ac.rs.Correlator;
import es.tid.fiware.iot.ac.xacml.Extractors;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.IOException;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.wso2.balana.PDP;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

@Path("/pdp/v3")
@Produces(MediaType.APPLICATION_XML)
public class PdpEndpoint {

    private final PdpFactory pdpFactory;
    private static final Logger LOGGER = LoggerFactory.getLogger(PdpEndpoint.class);

    public PdpEndpoint(PdpFactory pdpFactory) {
        this.pdpFactory = pdpFactory;
    }

    @POST
    @UnitOfWork(readOnly = true, transactional = false,
            cacheMode = CacheMode.GET, flushMode = FlushMode.MANUAL)
    @Timed
    public Response enforce(@Tenant String tenant,
                            @Correlator String correlator,
                            String xacmlRequest) {

        LOGGER.debug("Enforcing policies for tenant [{}]", tenant);
        LOGGER.trace("XACML Request: {}", xacmlRequest);
        Set<String> subjectIds = extractSubjectIds(xacmlRequest);
        LOGGER.debug("XACML Request subjectIds: {}", subjectIds);
        String evaluation = null;
        // Check if work in Pep-steelskin mode or not
        if (pdpFactory.getSteelSkinPepMode()) {
            // Iterate by all SubjectIs to evaluate by each subjectId
            for (String subjectId : subjectIds) {
                List<String> set_subjectId = new ArrayList<String>();
                set_subjectId.add(subjectId);
                PDP pdp = pdpFactory.get(tenant, new HashSet(set_subjectId));
                evaluation = pdp.evaluate(xacmlRequest);
                String response = extractDecision(evaluation);
                LOGGER.debug("XACML partial evaluation for Role {} is {}",
                             subjectId, response);
                if (response.equals("Permit")){
                    LOGGER.debug("XACML partial evaluation match for {}", subjectId);
                    LOGGER.debug("Skipping other subjects");
                    break;
                }
            }
        } else {
            PDP pdp = pdpFactory.get(tenant, extractSubjectIds(xacmlRequest));
            evaluation = pdp.evaluate(xacmlRequest);
            LOGGER.trace("XACML evaluation: {}", evaluation);
        }
        return Response.ok(evaluation).build();
    }

    private Set<String> extractSubjectIds(String xacmlRequest)
            throws WebApplicationException {
        try {
            return new HashSet(Extractors.extractSubjectIds(xacmlRequest));
        } catch (XPathExpressionException | IOException | SAXException e) {
            throw new WebApplicationException(400);
        }
    }

    private String extractDecision(String evaluation)
        throws WebApplicationException {
        try {
            String response = Extractors.extractDecision(evaluation);
            LOGGER.trace("response: {}", response);
            return response;
        } catch (XPathExpressionException | IOException | SAXException e) {
            throw new WebApplicationException(400);
        }
    }

}
