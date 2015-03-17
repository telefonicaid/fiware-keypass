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

import es.tid.fiware.iot.ac.util.Util;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class TestExtractors {

    @Test
    public void testExtractSubjects() throws Exception {
        String xacmlStr = Util.read(this.getClass(), "policy01_request03.xml");
        Collection<String> subj = Extractors.extractSubjectIds(xacmlStr);
        assertEquals(2, subj.size());
        assertTrue(subj.contains("role12345"));
        assertTrue(subj.contains("role2"));
    }

    @Test
    public void testExtractDecision() throws Exception {
        String xacmlStr = Util.read(this.getClass(), "response01.xml");
        String decision = Extractors.extractDecision(xacmlStr);
        assertEquals("Permit", decision);
    }

    @Test
    public void testExtractPolicyId() throws Exception {
        String xacmlStr = Util.read(this.getClass(), "policy01.xml");
        String policyId = Extractors.extractPolicyId(xacmlStr);
        assertEquals("policy01", policyId);
    }

    @Test
    public void testExtractPolicySetId() throws Exception {
        String xacmlStr = Util.read(this.getClass(), "policyset01.xml");
        String policySetId = Extractors.extractPolicySetId(xacmlStr);
        assertEquals("policyset01", policySetId);
    }

}
