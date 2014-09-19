package es.tid.fiware.iot.ac.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

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

@Entity
public class Policy {

    @Embeddable
    public static class PolicyId implements Serializable {
        private String id;
        private String tenant;
        
        public PolicyId() {
            
        }
        
        public PolicyId(String tenant, String id) {
            this.id = id;
            this.tenant = tenant;
        }
        
        public String getId() {
            return id;
        }
        
        public String getTenant() {
            return tenant;
        }
    }
    
    @EmbeddedId
    private PolicyId internalId;
    private String subject;

    private String policy;

    public Policy() {
        
    }

    public Policy(String id, String tenant, String subject, String policy) {
        this.internalId = new PolicyId(tenant, id);
        this.subject = subject;
        this.policy = policy;
    }

    public String getId() {
        return internalId.getId();
    }

    public PolicyId getInternalId() {
        return internalId;
    }

    public void setInternalId(PolicyId id) {
        internalId = id;
    }
    
    public String getTenant() {
        return internalId.getTenant();
    }

    public String getSubject() {
        return subject;
    }

    public String getPolicy() {
        return policy;
    }

}
