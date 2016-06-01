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

import es.tid.fiware.iot.ac.util.Xml;
import java.io.IOException;
import java.util.Collection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author dmj
 */
public class PolicySet {
    
    static private final String COMBINING_POLICY = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides";
    static private final String VERSION = "1.0";
    static private final String NAMESPACE = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17";
    
    private Collection<Policy> policies;
    private String id;
    
    public PolicySet(String policySetId, Collection<Policy> policies) {
        this.policies = policies;
        this.id = policySetId;
    }
    
    public Document toXml() throws IOException, SAXException {
        Document setDocument = Xml.newDocument();
        Element rootElement = setDocument.createElementNS(NAMESPACE, "PolicySet");
        setDocument.appendChild(rootElement);
        
        rootElement.setAttributeNS(NAMESPACE, "PolicySetId", id);
        rootElement.setAttributeNS(NAMESPACE, "Version", VERSION);
        rootElement.setAttributeNS(NAMESPACE, "PolicyCombiningAlgId", COMBINING_POLICY);
        
        for (Policy p: policies) {
            Document policyDocument = Xml.toXml(p.getPolicy());
            Element policyElement = policyDocument.getDocumentElement();
            rootElement.appendChild(setDocument.importNode(policyElement, true));
        }
        
        return setDocument;
    }

}
