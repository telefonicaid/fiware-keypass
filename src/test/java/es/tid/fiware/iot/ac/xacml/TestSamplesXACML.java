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

import es.tid.fiware.iot.ac.util.Xml;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.wso2.balana.PDP;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;


public class TestSamplesXACML {

    private PDPFactory pdpFactory = new PDPFactory();
    private XPath xpath = XPathFactory.newInstance().newXPath();

    @DataProvider(name = "policies")
    public Object[][] createPoliciesDataset() {
        return new Object[][] {
                { "One Policy, one Subject and Permit",
                  Arrays.asList("policy01.xml"),
                  "policy01_request01.xml", "Permit" },

                { "One Policy, one Subject and Deny",
                  Arrays.asList("policy01.xml"),
                  "policy01_request02.xml", "Deny" },

                { "Two Policy, one Subject and Permit",
                  Arrays.asList("policy01.xml", "policy02.xml"),
                  "policy01_request01.xml", "Permit" },

                { "One Policy, two Subjects and Permit",
                  Arrays.asList("policy01.xml"),
                  "policy01_request03.xml", "Permit" },

                { "Policy with resource target",
                  Arrays.asList("policy03.xml"),
                  "policy01_request01.xml", "Permit" }

        };
    }

    @Test(dataProvider = "policies")
    public void testPolicyEval(String testName, List<String> policies,
            String request, String decision) throws Exception {

        PDP pdp = createPDP(policies);

        String xacmlRes = pdp.evaluate(Util.read(request));

        assertEquals(decision, Extractors.extractDecision(xacmlRes));
    }

    private PDP createPDP(List<String> policiesFiles) throws Exception {
        List<Document> policies = new ArrayList<Document>();
        for (String policyFile : policiesFiles) {
            policies.add(Xml.toXml(Util.read(policyFile)));
        }
        return pdpFactory.build(policies);
    }

}
