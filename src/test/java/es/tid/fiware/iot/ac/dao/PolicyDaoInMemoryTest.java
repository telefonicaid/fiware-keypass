package es.tid.fiware.iot.ac.dao;

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

import es.tid.fiware.iot.ac.model.Policy;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.AssertJUnit.assertEquals;

public class PolicyDaoInMemoryTest {

    @Test
    public void testListAllFromTenant() {
        PolicyDao dao = new PolicyDaoInMemory();
        dao.createPolicy(new Policy("rule1", "myTenant", "subject1", "policyTwo"));
        dao.createPolicy(new Policy("rule2", "myTenant", "subject1", "policyTwo"));
        dao.createPolicy(new Policy("rule1", "otherTenant", "subject2", "policyTwo"));

        Collection<Policy> ps = dao.getPolicies("myTenant", "subject1");
        assertEquals(2, ps.size());
    }
}
