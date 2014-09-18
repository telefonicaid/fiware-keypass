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
                  "policy01_request03.xml", "Permit" }

        };
    }

    @Test(dataProvider = "policies")
    public void testPolicyEval(String testName, List<String> policies,
            String request, String decision) {

        PDP pdp = createPDP(policies);

        String xacmlRes = pdp.evaluate(read(request));

        assertEquals(decision, extractDecision(xacmlRes));
    }

    private PDP createPDP(List<String> policiesFiles) {
        List<Document> policies = new ArrayList<Document>();
        for (String policyFile : policiesFiles) {
            policies.add(Xml.toXml(read(policyFile)));
        }
        return pdpFactory.build(policies);
    }

    private String read(String f) {
        return new Scanner(TestSamplesXACML.class.getResourceAsStream(f)).useDelimiter("\\Z").next();
    }

    private String extractDecision(String xacmlRes) {
        try {
            return xpath.evaluate("//*[local-name()='Decision']", Xml.toXml(xacmlRes));
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

}
