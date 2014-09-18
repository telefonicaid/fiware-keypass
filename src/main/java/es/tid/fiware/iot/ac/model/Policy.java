package es.tid.fiware.iot.ac.model;

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

public class Policy {

    private final String id;

    private final String tenant;

    private final String subject;

    private final String policy;

    public Policy(String id, String tenant, String subject, String policy) {
        this.id = id;
        this.tenant = tenant;
        this.subject = subject;
        this.policy = policy;
    }

    public String getId() {
        return id;
    }

    public String getTenant() {
        return tenant;
    }

    public String getSubject() {
        return subject;
    }

    public String getPolicy() {
        return policy;
    }

}
