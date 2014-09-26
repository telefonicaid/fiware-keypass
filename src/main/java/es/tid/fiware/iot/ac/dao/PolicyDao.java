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

import java.util.Collection;

public interface PolicyDao {

    Policy createPolicy(Policy policy);

    Policy loadPolicy(String tenant, String subject, String id);

    Collection<Policy> getPolicies(String tenant, String subject);

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
