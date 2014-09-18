/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.tid.fiware.iot.ac.model;

import es.tid.fiware.iot.ac.util.Xml;
import java.io.IOException;
import java.util.Collection;
import javax.ws.rs.core.Response;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author dmj
 */
public class PolicySet {
    
    private Collection<Policy> policies;
    
    public PolicySet(Collection<Policy> policies) {
        this.policies = policies;
    }
    
    public Document toXml() throws IOException, SAXException {
        Document setDocument = Xml.newDocument();
        Element rootElement = setDocument.createElementNS("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17", "PolicySet");
        setDocument.appendChild(rootElement);
        
        for (Policy p: policies) {
            Document policyDocument = Xml.toXml(p.getPolicy());
            Element policyElement = policyDocument.getDocumentElement();
            rootElement.appendChild(setDocument.importNode(policyElement, true));
        }
        
        return setDocument;
    }

}
