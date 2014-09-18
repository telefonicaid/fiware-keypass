package es.tid.fiware.iot.ac.dao;
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
