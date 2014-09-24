package es.tid.fiware.iot.ac.xacml;

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

import org.w3c.dom.Document;
import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.impl.InMemoryPolicyFinderModule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PDPFactory {

    private final PDPConfig pdpConfig;

    public PDPFactory() {
        Balana balana = Balana.getInstance();
        pdpConfig = balana.getPdpConfig();
    }

    public PDP build(List<Document> policies) {
        PolicyFinder finder= new PolicyFinder();

        PolicyFinderModule testPolicyFinderModule = new InMemoryPolicyFinderModule(policies);
        Set<PolicyFinderModule> policyModules = new HashSet<PolicyFinderModule>();
        policyModules.add(testPolicyFinderModule);
        finder.setModules(policyModules);

        PDPConfig newConfig = new PDPConfig(pdpConfig.getAttributeFinder(), finder,
                pdpConfig.getResourceFinder(), false);
        return new PDP(newConfig);
    }
}
