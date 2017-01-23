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
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;

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

    private MetricRegistry metrics;

    public MetricsEndpoint(MetricRegistry metrics){
        this.metrics = metrics;
    }

    /**
     * Get Metrics
     *
     * @param reset
     * @return metrics
     */

    @GET
    @UnitOfWork
    @Correlator
    public Response getMetrics(@Correlator String correlator,
                               @QueryParam("reset") String reset
                               ) {

        JSONObject outputjson = _getMetrics();
        LOGGER.debug("Get metrics ");
        if (reset != null) {
            if (reset.toUpperCase().equals("TRUE")) {
                _resetMetrics();
            }
        }
        return Response.status(200).entity(outputjson.toString()).build();
    }


    /**
     * Reset Metrics
     *
     * @return
     */

    @DELETE
    @UnitOfWork
    @Correlator
    public Response resetMetrics(@Correlator String correlator
                                 ) {
        LOGGER.debug("Reset metrics ");
        JSONObject outputjson = _getMetrics();
        _resetMetrics();
        return Response.status(204).entity(outputjson.toString()).build();
    }



    private JSONObject _getMetrics() {

        // Metrics: no outgoing no subservice
        // incomingTransactions: number of requests consumed by the component.
        // incomingTransactionRequestSize: total size (bytes) in requests associated to incoming transactions
        // incomingTransactionResponseSize: total size (bytes) in responses associated to incoming transactions
        // incomingTransacionError: number of incoming transactions resulting in error.
        // serviceTime: average time to serve a transaction.


        Timer requests_All =
            metrics.timer("io.dropwizard.jetty.MutableServletContextHandler.requests");
        Meter responses_400 =
            metrics.meter("io.dropwizard.jetty.MutableServletContextHandler.4xx-responses");
        Meter responses_500 =
            metrics.meter("io.dropwizard.jetty.MutableServletContextHandler.5xx-responses");


        // Get size of request and responses
        Counter incomingTransactionRequestSizeCounter =
            metrics.counter("incomingTransactionRequestSize");
        Counter incomingTransactionResponseSizeCounter =
            metrics.counter("incomingTransactionResponseSize");

        Counter requests_before_reset =
            metrics.counter("requests_before_reset");
        Counter responses_error_before_reset =
            metrics.counter("responses_error__before_reset");


        long errors = (long)responses_400.getCount() +
            (long)responses_500.getCount() -
            responses_error_before_reset.getCount();
        long requests = (long)requests_All.getCount() -
            (long)requests_before_reset.getCount() -
            errors;


        JSONObject outputjson = new JSONObject();
        outputjson.put("service", new JSONObject());
        JSONObject sumlist = new JSONObject();
        sumlist.put("incomingTransactions", requests);
        sumlist.put("incomingTransactionRequestSize",
                    incomingTransactionRequestSizeCounter.getCount());
        sumlist.put("incomingTransactionResponseSize",
                    incomingTransactionResponseSizeCounter.getCount());
        sumlist.put("incomingTransactionErrors", errors);
        sumlist.put("serviceTime", requests_All.getMeanRate());
        outputjson.put("sum", sumlist);

        return outputjson;
    }


    private void _resetMetrics() {
        // Reset custom counters
        Counter incomingTransactionRequestSizeCounter =
            metrics.counter("incomingTransactionRequestSize");
        long incomingTransactionRequestSizeCount =
            incomingTransactionRequestSizeCounter.getCount();
        incomingTransactionRequestSizeCounter.dec(incomingTransactionRequestSizeCount);

        Counter incomingTransactionResponseSizeCounter =
            metrics.counter("incomingTransactionResponseSize");
        long incomingTransactionResponseSizeCount =
            incomingTransactionResponseSizeCounter.getCount();
        incomingTransactionResponseSizeCounter.dec(incomingTransactionResponseSizeCount);


        metrics.remove("requests_before_reset");
        metrics.counter("requests_before_reset").inc(
            metrics.timer("io.dropwizard.jetty.MutableServletContextHandler.requests").getCount());

        metrics.remove("responses_error_befor_reset");
        metrics.counter("responses_error_befor_reset").inc(
            metrics.meter("io.dropwizard.jetty.MutableServletContextHandler.4xx-responses").getCount() +
            metrics.meter("io.dropwizard.jetty.MutableServletContextHandler.5xx-responses").getCount()
        );



    }

}
