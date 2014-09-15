package es.tid.fiware.iot.ac.xacml;
/*
 * Telefónica Digital - Product Development and Innovation
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Copyright (c) Telefónica Investigación y Desarrollo S.A.U.
 * All rights reserved.
 */

import org.w3c.dom.Document;
import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;

import java.util.Arrays;
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
