# API Usage

## Introduction

Keypass is multi-tenant XACML server with PAP (Policy Administration Point) and
PDP (Policy Detention Point) capabilities.

The PDP endpoint will evaluate the Policies for the subjects contained in a
XACML request. This is a design decision took by Keypass in order to simplify
how the application is used.

You, as a developer, may wonder what a subject is, and why policies are grouped
around them. To simplify, a subject is the same you put in a `subject-id` in
an XACML request. You can then structure your user, groups and roles as usual
in your preferred Identity Management system, just taking into account that
those `ids` (subject, roles, groups) shall be used in your PEP when building
the XACML request.

Applying the policies per subject means that policies must be managed grouping
them by subject. Keypass PAP API is designed to accomplish this.

As XACML is an XML specification, Keypass API offers an XML Restful API.

From the PAP REST point of view, the only resource is the Policy, with resides
in a Subject of a Tenant. Both Tenant and Subject may be seen as namespaces, as
they are not resources _per se_.

## PAP API

### Create or update a policy

```
POST /pap/v1/:tenant/subject/:subjectId

<Policy/>
```

Creates or updates a policy. Uses XACML `PolicyId` attribute as policy identifier, used
in other methods calls to retrieve or delete a single policy.

The given `PolicyId` is unique within the tenant. In case it already exists is
replaced (updated) with the new policy.

Examples:

```HTTP
POST /pap/v1/myTenant/subject/role12345 HTTP/1.1
Content-type: application/xml
Accept: application/xml

<Policy xsi:schemaLocation="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17
    http://docs.oasis-open.org/xacml/3.0/xacml-core-v3-schema-wd-17.xsd"
        PolicyId="policy03"
        RuleCombiningAlgId="urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit"
        Version="1.0" xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

  <Target>
    <AnyOf>
      <AllOf>
        <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-regexp-match">
          <AttributeValue
              DataType="http://www.w3.org/2001/XMLSchema#string"
              >fiware:orion:.*</AttributeValue>
          <AttributeDesignator
              AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"
              MustBePresent="true"
              Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource" />
        </Match>
      </AllOf>
    </AnyOf>
  </Target>

  <Rule RuleId="policy03rule01" Effect="Permit">

    <Condition>
      <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
          <AttributeDesignator
              AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"
              MustBePresent="true"
              Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action" />
        </Apply>
        <AttributeValue
            DataType="http://www.w3.org/2001/XMLSchema#string"
            >read</AttributeValue>
      </Apply>
    </Condition>
  </Rule>

</Policy>
```

Response

```HTTP
HTTP/1.1 201 Created
Location: http://localhost:8080/pap/v1/myTenant/subject/role12345/policy/policy03
```

<!--
TODO: Add duplicate 409 examples.
 -->

### Get a policy

```
GET /pap/v1/:tenant/subject/:subjectId/policy/:policyId
```

Retrieves an existing policy. Returns `404` if the policy does not exist. Please
note that `404` is returned also if the Tenant or Subject does not exists.

Example:

```
GET http://localhost:8080/pap/v1/myTenant/subject/role12345/policy/policy03
```

Response

```HTTP
HTTP/1.1 200 OK
Content-Type: application/xml

<Policy xsi:schemaLocation="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17
    http://docs.oasis-open.org/xacml/3.0/xacml-core-v3-schema-wd-17.xsd"
        PolicyId="policy03"
        RuleCombiningAlgId="urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit"
        Version="1.0" xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

  <Target>
    <AnyOf>
      <AllOf>
        <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-regexp-match">
          <AttributeValue
              DataType="http://www.w3.org/2001/XMLSchema#string"
              >fiware:orion:.*</AttributeValue>
          <AttributeDesignator
              AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"
              MustBePresent="true"
              Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource" />
        </Match>
      </AllOf>
    </AnyOf>
  </Target>

  <Rule RuleId="policy03rule01" Effect="Permit">

    <Condition>
      <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
          <AttributeDesignator
              AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"
              MustBePresent="true"
              Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action" />
        </Apply>
        <AttributeValue
            DataType="http://www.w3.org/2001/XMLSchema#string"
            >read</AttributeValue>
      </Apply>
    </Condition>
  </Rule>

</Policy>
```

### Delete a policy

```
DELETE /pap/v1/:tenant/subject/:subjectId/policy/:policyId
```

Removes a policy. If removed successfully, returns the removed policy. In case
the policy does not exists, returns `404`.


Example:

```
DELETE http://localhost:8080/pap/v1/myTenant/subject/role12345/policy/policy03
```

Response

```HTTP
HTTP/1.1 200 OK
Content-Type: application/xml

<Policy xsi:schemaLocation="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17
    http://docs.oasis-open.org/xacml/3.0/xacml-core-v3-schema-wd-17.xsd"
        PolicyId="policy03"
        RuleCombiningAlgId="urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit"
        Version="1.0" xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

  <Target>
    <AnyOf>
      <AllOf>
        <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-regexp-match">
          <AttributeValue
              DataType="http://www.w3.org/2001/XMLSchema#string"
              >fiware:orion:.*</AttributeValue>
          <AttributeDesignator
              AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"
              MustBePresent="true"
              Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource" />
        </Match>
      </AllOf>
    </AnyOf>
  </Target>

  <Rule RuleId="policy03rule01" Effect="Permit">

    <Condition>
      <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
          <AttributeDesignator
              AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id"
              DataType="http://www.w3.org/2001/XMLSchema#string"
              MustBePresent="true"
              Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action" />
        </Apply>
        <AttributeValue
            DataType="http://www.w3.org/2001/XMLSchema#string"
            >read</AttributeValue>
      </Apply>
    </Condition>
  </Rule>

</Policy>
```

### Get subject policies

```
GET /pap/v1/:tenant/subject/:subjectId
```

Retrieves all the policies of a given subject as PolicySet element. If there
are policies, returns a empty policy set. Please note that as stated in previous
sections, the tenant nor the subject are entities by themselves, so getting the
policies of non existent tenant or subject will return a valid PolicySet with no
policies.

Exmple:

```
GET http://localhost:8080/pap/v1/myTenant/subject/role12345
```

Response

```HTTP
HTTP/1.1 200 OK
Content-Type: application/xml

<PolicySet xmlns:ns0="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" xmlns:ns1="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" ns0:PolicySetId="urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides" ns1:Version="1.0"/>
```

### Delete subject policies

```
DELETE /pap/v1/:tenant/subject/:subjectId
```

Convenience method to remove all the policies of the subject. Will return `204`
with an empty body, even for already empty or non-existent subjects (remember,
a subject is not a resource from the Keypass point of view).

Example:

```
DELETE http://localhost:8080/pap/v1/myTenant/subject/role12345
```

Response

```HTTP
HTTP/1.1 204 No Content
```

### Delete tenant policies

```
DELETE /pap/v1/:tenant
```

Convenience method to remove all the policies of the given tenant. As previous
method, returns `204` always.

Example:

```
DELETE http://localhost:8080/pap/v1/myTenant
```

Response

```HTTP
HTTP/1.1 204 No Content
```


## PDP API

```
POST /pdp/v3/:tenant

<xacmlRequest/>
```

Evaluates the given request against the policies for the requests subjects. In
case the tenant or the subjects does not exists will return an XACML response
with Decision `NotApplicable`.

Example:

```HTTP
POST /pdp/v3/myTenant
Content-type: application/xml
Accept: application/xml

<Request xsi:schemaLocation="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17 http://docs.oasis-open.org/xacml/3.0/xacml-core-v3-schema-wd-17.xsd" ReturnPolicyIdList="false" CombinedDecision="false" xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <Attributes Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject">
    <Attribute IncludeInResult="false" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">role12345</AttributeValue>
    </Attribute>
  </Attributes>
  <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource">
    <Attribute IncludeInResult="false" AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">fiware:orion:tenant1234:us-west-1:res9876</AttributeValue>
    </Attribute>
  </Attributes>
  <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action">
    <Attribute IncludeInResult="false" AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id">
      <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">read</AttributeValue>
    </Attribute>
  </Attributes>
</Request>
```

Response

```HTTP
HTTP/1.1 200 OK
Content-Type: application/xml

<Response xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17">
  <Result>
    <Decision>NotApplicable</Decision>
    <Status>
      <StatusCode Value="urn:oasis:names:tc:xacml:1.0:status:ok"/>
    </Status>
  </Result>
</Response>
```
