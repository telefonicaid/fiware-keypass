package org.wso2.balana.finder.impl;

/*
*  Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import java.net.URI;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.DOMHelper;
import org.wso2.balana.MatchResult;
import org.wso2.balana.ParsingException;
import org.wso2.balana.Policy;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.PolicyReference;
import org.wso2.balana.PolicySet;
import org.wso2.balana.VersionConstraints;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.xacml2.DenyOverridesPolicyAlg;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;

/**
 * An in-memory policy repository, in which policies are maintained in a static List.
 *
 */
public class InMemoryPolicyFinderModule extends PolicyFinderModule {

    private PolicyFinder finder = null;
    private PolicyCombiningAlgorithm combiningAlg;
    private Map<URI, AbstractPolicy> policies;

    private static Log log = LogFactory.getLog(InMemoryPolicyFinderModule.class);

    /**
     * Instantiate a repository using the specified collection of policy documents.
     * @param policyDocuments the list of policy documents
     */
    public InMemoryPolicyFinderModule(List<Document> policyDocuments) {
        policies = new LinkedHashMap<URI, AbstractPolicy>();
        for (Document d : policyDocuments) {
            AbstractPolicy p = loadPolicy(d);
            policies.put(p.getId(), p);
        }
        combiningAlg = new DenyOverridesPolicyAlg();
    }


    /**
     * Instantiate a repository using the specified collection of policy documents.
     * @param policyDocuments the list of policy documents
     * @param combiningAlg policy combining algorithm <code>PolicyCombiningAlgorithm</code>
     */
    public InMemoryPolicyFinderModule(List<Document> policyDocuments, PolicyCombiningAlgorithm combiningAlg) {
        policies = new HashMap<URI, AbstractPolicy>();
        for (Document d : policyDocuments) {
            AbstractPolicy p = loadPolicy(d);
            policies.put(p.getId(), p);
        }
        this.combiningAlg = combiningAlg;
    }

    @Override
    public void init(PolicyFinder finder) {
        this.finder = finder;
    }

    @Override
    public boolean isIdReferenceSupported() {
        return true;
    }

    @Override
    public boolean isRequestSupported() {
        return true;
    }

    // TODO: Copied these 2 from the FileBasedPolicyFinderModule class; consider pulling these up into the abstract base?

    @Override
    public PolicyFinderResult findPolicy(EvaluationCtx context) {

        ArrayList<AbstractPolicy> selectedPolicies = new ArrayList<AbstractPolicy>();
        Set<Map.Entry<URI, AbstractPolicy>> entrySet = policies.entrySet();

        // iterate through all the policies we currently have loaded
        for (Map.Entry<URI, AbstractPolicy> entry : entrySet) {
            AbstractPolicy policy = entry.getValue();
            MatchResult match = policy.match(context);
            int result = match.getResult();

            // if target matching was indeterminate, then return the error
            if (result == MatchResult.INDETERMINATE)
                return new PolicyFinderResult(match.getStatus());

            // see if the target matched
            if (result == MatchResult.MATCH) {

                if ((combiningAlg == null) && (selectedPolicies.size() > 0)) {
                    // we found a match before, so this is an error
                    ArrayList<String> code = new ArrayList<String>();
                    code.add(Status.STATUS_PROCESSING_ERROR);
                    Status status = new Status(code, "too many applicable " + "top-level policies");
                    return new PolicyFinderResult(status);
                }

                // this is the first match we've found, so remember it
                selectedPolicies.add(policy);
            }
        }

        // no errors happened during the search, so now take the right
        // action based on how many policies we found
        switch (selectedPolicies.size()) {
            case 0:
                if (log.isDebugEnabled()) {
                    log.debug("No matching XACML policy found");
                }
                return new PolicyFinderResult();
            case 1:
                return new PolicyFinderResult((selectedPolicies.get(0)));
            default:
                return new PolicyFinderResult(new PolicySet(null, combiningAlg, null, selectedPolicies));
        }
    }

    @Override
    public PolicyFinderResult findPolicy(URI idReference, int type, VersionConstraints constraints, PolicyMetaData parentMetaData) {

        AbstractPolicy policy = policies.get(idReference);
        if (policy != null) {
            if (type == PolicyReference.POLICY_REFERENCE) {
                if (policy instanceof Policy) {
                    return new PolicyFinderResult(policy);
                }
            } else {
                if (policy instanceof PolicySet) {
                    return new PolicyFinderResult(policy);
                }
            }
        }

        // if there was an error loading the policy, return the error
        ArrayList<String> code = new ArrayList<String>();
        code.add(Status.STATUS_PROCESSING_ERROR);
        Status status = new Status(code, "couldn't load referenced policy");
        log.info("No policy found, code=" + code);
        return new PolicyFinderResult(status);
    }

    private AbstractPolicy loadPolicy(Document policyDocument) {
        // based this largely on the FileBasedPolicyFinderModule implementation...strong potential for refactoring / pull-up here...
        AbstractPolicy policy = null;
        Element root = policyDocument.getDocumentElement();
        String name = DOMHelper.getLocalName(root);
        try {
            if (name.equals("Policy")) {

                policy = Policy.getInstance(root);

            } else if (name.equals("PolicySet")) {
                policy = PolicySet.getInstance(root, finder);
            }
        } catch (ParsingException e) {
            // just only logs
            log.error("Fail to load policy : " + policyDocument.getDocumentElement().getNodeName(), e);
        }
        return policy;
    }

}
