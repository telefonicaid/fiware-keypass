package es.tid.fiware.iot.ac.dao;/*
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

import javax.annotation.Nullable;
import java.util.Collection;

public interface PolicyDao {

    public Policy create(String tenant, @Nullable String subject, String policy);

    public Policy get(String tenant, @Nullable String subject, String id);

    public Collection<Policy> list(String tenant, @Nullable String subject);

    public Collection<Policy> listAll(String tenant);

    public Policy update(Policy policy);

    public Policy delete(Policy policy);

}
