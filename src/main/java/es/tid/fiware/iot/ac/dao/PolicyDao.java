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

import java.util.Collection;

public interface PolicyDao {

    Policy createPolicy(Policy policy);

    Policy loadPolicy(String tenant, String subject, String id);

    Collection<Policy> getPolicies(String tenant, String subject);

    /**
     * Return all the Subjects for a given Tenant.
     * TODO: paginate?
     */
    Collection<String> getSubjects(String tenant);

    Policy updatePolicy(Policy policy);

    Policy deletePolicy(Policy policy);

    /**
     * Removes the Tenant and all related objects: Tenant's Subjects and
     * Policies.
     */
    void deleteFromTenant(String tenant);

    /**
     * Removes the Subject from the Tenant, and its Policies.
     */
    void deleteFromSubject(String tenant, String subject);
}
