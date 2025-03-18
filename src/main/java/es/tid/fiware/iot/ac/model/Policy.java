package es.tid.fiware.iot.ac.model;

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

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "\"Policy\"")
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
