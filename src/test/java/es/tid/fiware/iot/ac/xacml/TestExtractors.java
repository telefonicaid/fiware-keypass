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

import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class TestExtractors {

    @Test
    public void testExtractSubjects() throws Exception {
        String xacmlStr = Util.read("policy01_request03.xml");
        Collection<String> subj = Extractors.extractSubjectIds(xacmlStr);
        assertEquals(2, subj.size());
        assertTrue(subj.contains("role12345"));
        assertTrue(subj.contains("role2"));
    }

    @Test
    public void testExtractDecision() throws Exception {
        String xacmlStr = Util.read("response01.xml");
        String decision = Extractors.extractDecision(xacmlStr);
        assertEquals("Permit", decision);
    }

    @Test
    public void testExtractPolicyId() throws Exception {
        String xacmlStr = Util.read("policy01.xml");
        String policyId = Extractors.extractPolicyId(xacmlStr);
        assertEquals("policy01", policyId);
    }

}
