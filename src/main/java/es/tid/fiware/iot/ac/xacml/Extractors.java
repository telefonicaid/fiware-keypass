package es.tid.fiware.iot.ac.xacml;

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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.xpath.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Some XACML utilities methods.
 */
public class Extractors {

    private static XPath xpath = XPathFactory.newInstance().newXPath();

    private static XPathExpression sujectIdsExp =
            buildExp("//*[@AttributeId='urn:oasis:names:tc:xacml:1.0:subject:subject-id']/*[local-name()='AttributeValue']");

    private static XPathExpression decisionExp =
            buildExp("//*[local-name()='Decision']/text()");

    private static XPathExpression policyIdExp =
            buildExp("//*[local-name()='Policy']/@PolicyId");

    public static Collection<String> extractSubjectIds(String xacmlRequest)
            throws XPathExpressionException, IOException, SAXException {

        Document xacml = Xml.toXml(xacmlRequest);

        NodeList nodes = (NodeList) sujectIdsExp.evaluate(xacml,
                XPathConstants.NODESET);

        List<String> subjects = new ArrayList<String>();
        for (int i = 0; i < nodes.getLength(); i++) {
            subjects.add(nodes.item(i).getTextContent());
        }

        return subjects;
    }

    public static String extractDecision(String xacmlRes)
            throws XPathExpressionException, IOException, SAXException {
        return decisionExp.evaluate(Xml.toXml(xacmlRes));
    }

    public static String extractPolicyId(String xacmlPolicy)
            throws XPathExpressionException, IOException, SAXException {
        return policyIdExp.evaluate(Xml.toXml(xacmlPolicy));
    }

    private static XPathExpression buildExp(String exp) {
        try {
            return xpath.compile(exp);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

}
