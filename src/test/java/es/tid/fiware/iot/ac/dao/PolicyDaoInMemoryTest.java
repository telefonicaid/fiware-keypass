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
        dao.create("myTenant", null, "policyOne");
        dao.create("myTenant", null, "policyOneBis");
        dao.create("myTenant", "subject1", "policyTwo");
        dao.create("myTenant", "subject1", "policyTwoBis");
        dao.create("myTenant", "subject2", "policyThree");

        Collection<Policy> ps = dao.listAll("myTenant");
        assertEquals(5, ps.size());
    }

    @Test
    public void testTenantAndNoSubject() {
        PolicyDao dao = new PolicyDaoInMemory();
        Policy p0 = dao.create("myTenant", null, "policyOne");

        Policy p1 = dao.get("myTenant", null, p0.getId());
        assertEquals("policyOne", p1.getPolicy());

        Collection<Policy> p2s = dao.list("myTenant", null);
        assertEquals(1, p2s.size());
        assertEquals(p2s.iterator().next().getPolicy(), "policyOne");
    }
}
