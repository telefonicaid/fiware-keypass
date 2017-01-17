package es.tid.fiware.iot.ac;

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

import es.tid.fiware.iot.ac.dao.PolicyDAOHibernate;
import es.tid.fiware.iot.ac.dao.PolicyDao;
import es.tid.fiware.iot.ac.model.Policy;
import es.tid.fiware.iot.ac.pap.PoliciesEndpoint;
import es.tid.fiware.iot.ac.pap.SubjectEndpoint;
import es.tid.fiware.iot.ac.pap.TenantEndpoint;
import es.tid.fiware.iot.ac.pdp.PdpEndpoint;
import es.tid.fiware.iot.ac.pdp.PdpFactory;
import es.tid.fiware.iot.ac.pdp.PdpFactoryCached;
import es.tid.fiware.iot.ac.rs.TenantHeaderFilter;
import es.tid.fiware.iot.ac.rs.TenantProvider;
import es.tid.fiware.iot.ac.rs.CorrelatorHeaderFilter;
import es.tid.fiware.iot.ac.rs.CorrelatorProvider;
import es.tid.fiware.iot.ac.util.BlockingCacheFactory;
import es.tid.fiware.iot.ac.util.LogsEndpoint;
import es.tid.fiware.iot.ac.util.VersionEndpoint;
import es.tid.fiware.iot.ac.util.MetricsEndpoint;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import com.sun.jersey.api.client.Client;


public class AcService extends io.dropwizard.Application<AcConfig> {

    private final HibernateBundle<AcConfig> hibernate = new HibernateBundle<AcConfig>(Policy.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(AcConfig configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(String[] args) throws Exception {
        new AcService().run(args);
    }

    @Override
    public void initialize(Bootstrap<AcConfig> bootstrap) {
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(new MigrationsBundle<AcConfig>() {
            @Override
            public DataSourceFactory getDataSourceFactory(AcConfig configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(AcConfig configuration,
            Environment environment)
            throws Exception {

        final Client jerseyClient = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration())
            .build(getName());

        PolicyDao dao = new PolicyDAOHibernate(hibernate.getSessionFactory());
        PdpFactory pdpFactory = new PdpFactoryCached(dao,
                new BlockingCacheFactory(
                        configuration.getPdpCacheConfig().getTimeToLiveSeconds(),
                        configuration.getPdpCacheConfig().getMaxEntriesLocalHeap()),
                                                     configuration.getSteelSkinPepMode());

        environment.servlets().addFilter("myFilter", new MDCFilter()).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        environment.jersey().register(new TenantProvider(configuration.getTenantHeader()));
        environment.jersey().register(new CorrelatorProvider(configuration.getCorrelatorHeader()));
        environment.jersey().getResourceConfig().getContainerResponseFilters().add(
                new TenantHeaderFilter(configuration.getTenantHeader()));
        environment.jersey().getResourceConfig().getContainerResponseFilters().add(
                new CorrelatorHeaderFilter(configuration.getCorrelatorHeader()));

        environment.jersey().register(new TenantEndpoint(dao));
        environment.jersey().register(new SubjectEndpoint(dao));
        environment.jersey().register(new PoliciesEndpoint(dao));
        environment.jersey().register(new PdpEndpoint(pdpFactory));
        environment.jersey().register(new LogsEndpoint());
        environment.jersey().register(new VersionEndpoint());
        environment.jersey().register(new MetricsEndpoint(jerseyClient,
                                                          ((HttpConnectorFactory) ((DefaultServerFactory) configuration.getServerFactory()).getAdminConnectors().get(0)).getPort()));

    }
}
