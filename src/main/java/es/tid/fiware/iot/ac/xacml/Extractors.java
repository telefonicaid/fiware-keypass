package es.tid.fiware.iot.ac.xacml;/*
 * Telefónica Digital - Product Development and Innovation
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Copyright (c) Telefónica Investigación y Desarrollo S.A.U.
 * All rights reserved.
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
