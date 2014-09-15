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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.wso2.balana.PDP;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.testng.AssertJUnit.assertTrue;


public class TestSamplesXACML {

    private PDPFactory pdpFactory = new PDPFactory();

    @DataProvider(name = "policies")
    public Object[][] createPoliciesDataset() {
        return new Object[][] {
                { Arrays.asList("policy01.xml"),
                  "policy01_request01.xml", "Permit" },
                { Arrays.asList("policy01.xml"),
                  "policy01_request02.xml", "Deny" },
        };
    }

    @Test(dataProvider = "policies")
    public void testPolicyEval(List<String> policies, String request, String decision) {

        PDP pdp = createPDP(policies);
        String eval = pdp.evaluate(read(request));

        assertTrue(eval.contains(decision));
    }

    private PDP createPDP(List<String> policiesFiles) {
        List<Document> policies = new ArrayList<Document>();
        for (String policyFile : policiesFiles) {
            policies.add(toXml(read(policyFile)));
        }
        return pdpFactory.build(policies);
    }

    private String read(String f) {
        return new Scanner(TestSamplesXACML.class.getResourceAsStream(f)).useDelimiter("\\Z").next();
    }

    private Document toXml(String str) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setNamespaceAware(true);
            DocumentBuilder db = factory.newDocumentBuilder();
            return db.parse(new InputSource(new StringReader(str)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
