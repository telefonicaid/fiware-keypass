package es.tid.fiware.iot.ac.util;

/*
 * Copyright 2017 Telefonica Investigación y Desarrollo, S.A.U
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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Path("/admin/metrics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MetricsEndpoint {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(LogsEndpoint.class);

    private Client jerseyClient;
    private Integer adminPort;
    private String metricsAdminPath;

    public MetricsEndpoint(Client jerseyClient, Integer adminPort) {
        this.jerseyClient = jerseyClient;
        this.adminPort = adminPort;
        this.metricsAdminPath = new String("http://127.0.0.1:" + this.adminPort + "/metrics");
    }

    /**
     * Get Metrics
     *
     * @param reset
     * @return metrics
     */

    @GET
    @UnitOfWork
    public Response getMetrics(@Correlator String correlator,
                               @QueryParam("reset") String level
                               ) {
        // Metrics: no outgoing no subservice
        // incomingTransactions: number of requests consumed by the component.
        // incomingTransactionRequestSize: total size (bytes) in requests associated to incoming transactions
        // incomingTransactionResponseSize: total size (bytes) in responses associated to incoming transactions
        // incomingTransacionError: number of incoming transactions resulting in error.
        // serviceTime: average time to serve a transaction.

        WebResource webResource = jerseyClient.resource(this.metricsAdminPath);
        ClientResponse response =  webResource.accept("application/json").type("application/json").get(ClientResponse.class);
        int status = response.getStatus();
        if (response.getStatus() != 200) {
            LOGGER.error("trying to change log level changed to: " + response.getStatus());
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String jettyMetrics = response.getEntity(String.class);
        JSONObject jettyMetricsjson = null;
        try {
            JSONParser parser = new JSONParser();
            jettyMetricsjson = (JSONObject) parser.parse(jettyMetrics);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject timers = (JSONObject) jettyMetricsjson.get("timers");
        JSONObject requests = (JSONObject) timers.get("io.dropwizard.jetty.MutableServletContextHandler.requests");

        // Matching jetty metrics with IoTPlatform metrics
        JSONObject outputjson = new JSONObject();
        outputjson.put("service", "TBD");
        JSONObject sumlist = new JSONObject();
        sumlist.put("incomingTransactions", requests.get("count"));
        sumlist.put("incomingTransactionRequestSize", new Integer(0));
        sumlist.put("incomingTransactionResponseSize", new Integer(0));
        sumlist.put("incomingTransactionErrors", new Integer(0));
        sumlist.put("serviceTime", requests.get("mean"));
        outputjson.put("sum", sumlist);

        return Response.status(200).entity(outputjson.toString()).build();
    }


    // /**
    //  * Reset Metrics
    //  *
    //  * @return
    //  */

    // @DELETE
    // @UnitOfWork
    // public Response resetMetrics(@Correlator String correlator,
    //                              ) {



    // }


}
