from lettuce import *
from uuid import uuid4
import requests
from lxml import etree
import pystache
from nose.tools import assert_equals

@step('I send a policy creation request to the Access Control for tenant "([^"]*)" and subject "([^"]*)"')
def sendPolicyCreationRequest(step, tenant, subject):
  url = world.config['targetUrl'] + '/pap/v1/subject/' + subject
  fRequest = open('./requests/policy.xml', 'r')
  payload = pystache.render(fRequest.read(), {'ruleId': str(uuid4())})
  headers = {'content-type': 'application/xml', world.config['tenantHeader']: tenant}
  r = requests.post(url, data=payload, headers=headers)
  world.retrievedRequest = r
  world.lastPolicyLocation = r.headers.get('Location')

@step('I send a invalid policy for tenant "([^"]*)" and subject "([^"]*)"')
def sendPolicyCreationRequest(step, tenant, subject):
    url = world.config['targetUrl'] + '/pap/v1/subject/' + subject
    fRequest = open('./requests/invalid-policy.xml', 'r')
    payload = fRequest.read()
    headers = {'content-type': 'application/xml', world.config['tenantHeader']: tenant}
    r = requests.post(url, data=payload, headers=headers)
    world.retrievedRequest = r

@step('the Access Control returns a "([^"]*)"')
def checkAccessControlReturnsOkAndId(step, code):
    assert_equals(int(code), world.retrievedRequest.status_code)

@step('the Access Control returns a "([^"]*)" code and a payload with the ID')
def checkAccessControlReturnsOkAndId(step, code):
  assert world.retrievedRequest.status_code == int(code)

@step('I can retrieve the created policy for tenant "([^"]*)" from the Access Control')
def retrieveCreatedPolicy(step, tenant):
  location = world.retrievedRequest.headers.get('Location')
  r = requests.get(location, headers={world.config['tenantHeader']: tenant})
  assert r.status_code == 200
  
@step('I send a validation request for tenant "([^"]*)" with subject "([^"]*)", FRN "([^"]*)" and action "([^"]*)"')
def sendValidationRequest(step, tenant, subject, frn, action):
  url = world.config['targetUrl'] + '/pdp/v3'
  fRequest = open('./requests/request.xml', 'r')
  payload = pystache.render(fRequest.read(), {'subject' : subject, 'frn': frn, 'action': action}) 
  headers = {'content-type': 'application/xml', world.config['tenantHeader']: tenant}
  r = requests.post(url, data=payload, headers=headers)
  world.retrievedRequest = r

@step('I send a validation request for tenant "([^"]*)" with subjects "([^"]*)" and "([^"]*)", FRN "([^"]*)" and action "([^"]*)"')
def sendValidationRequest(step, tenant, subject, subject2, frn, action):
    url = world.config['targetUrl'] + '/pdp/v3'
    fRequest = open('./requests/request-two-subjects.xml', 'r')
    payload = pystache.render(fRequest.read(), {'subject': subject, 'subject2': subject2, 'frn': frn, 'action': action})
    headers = {'content-type': 'application/xml', world.config['tenantHeader']: tenant}
    r = requests.post(url, data=payload, headers=headers)
    world.retrievedRequest = r


@step('Then the Access Control should "([^"]*)" the access')
def accessControlDecidesAction(step, decision):
  assert world.retrievedRequest.status_code == 200
  requestXML = etree.XML(world.retrievedRequest.text)
  decisionAC = requestXML.xpath('//t:Decision', namespaces={'t': 'urn:oasis:names:tc:xacml:3.0:core:schema:wd-17'})
  assert len(decisionAC) == 1
  assert_equals(decision, decisionAC[0].text)

@step('I send a remove request for the last request for tenant "([^"]*)"')
def removeLastRequest(step, tenant):
  location = world.retrievedRequest.headers.get('Location')
  r = requests.delete(location, headers={world.config['tenantHeader']: tenant})
  world.removalResult = r
  #world.retrievedRequest = r

@step('I send a remove request for tenant "([^"]*)"')
def removeTenant(step, tenant):
  url = world.config['targetUrl'] + '/pap/v1'
  r = requests.delete(url, headers={world.config['tenantHeader']: tenant})
  world.removalResult = r
  #world.retrievedRequest = r  

@step('I send a remove request for subject "([^"]*)" in tenant "([^"]*)"')
def removeSubject(step, subject, tenant):
  url = world.config['targetUrl'] + '/pap/v1/subject/' + subject
  r = requests.delete(url, headers={world.config['tenantHeader']: tenant})
  #world.removalResult = r
  world.retrievedRequest = r
  
@step('I get the list of policies for the tenant "([^"]*)" and subject "([^"]*)"')
def getTenantPolicies(step, tenant, subject):
  url = world.config['targetUrl'] + '/pap/v1/subject/' + subject
  r = requests.get(url, headers={world.config['tenantHeader']: tenant})
  world.requestList = r    

@step('trying to get the policy for tenant "([^"]*)" raises a 404')
def getLastPolicy(step, tenant):
  location = world.retrievedRequest.headers.get('Location')
  r = requests.get(location, headers={world.config['tenantHeader']: tenant})
  assert r.status_code == 404

@step('I send the following policies to the access control')
def sendPoliciesToAccessControl(step):
  for policy in step.hashes:
    tenant = policy['tenant']
    subject = policy['subject']
    url = world.config['targetUrl'] + '/pap/v1/subject/' + subject
    fRequest = open('./requests/policy.xml', 'r')
    payload = pystache.render(fRequest.read(), {'ruleId': policy['id']})
    headers = {'content-type': 'application/xml', world.config['tenantHeader']: tenant}
    r = requests.post(url, data=payload, headers=headers)

@step('the number of policies in the list is "([^"]*)"')
def testNumberOfPolicies(step, number):
  assert world.requestList.status_code == 200
  listXML = etree.XML(world.requestList.text)
  policies = listXML.xpath('//t:Policy', namespaces={'t': 'urn:oasis:names:tc:xacml:3.0:core:schema:wd-17'})
  assert_equals(len(policies), int(number))

def getLastRequestId(location, tenant):
  r = requests.get(location, headers={world.config['tenantHeader']: tenant})
  policyData = etree.XML(r.text)
  policyElement =  policyData.xpath('//t:Policy', namespaces={'t': 'urn:oasis:names:tc:xacml:3.0:core:schema:wd-17'})[0]
  return policyElement.get('PolicyId')

@step('I modify the policy for tenant "([^"]*)"')
def modifyPolicy(step, tenant):
  location = world.lastPolicyLocation
  policyId = getLastRequestId(location, tenant)
  fRequest = open('./requests/policyModified.xml', 'r')
  payload = pystache.render(fRequest.read(), {'ruleId': policyId})
  headers = {'content-type': 'application/xml', world.config['tenantHeader']: tenant}
  r = requests.put(location, data=payload, headers=headers)
  world.retrievedRequest = r
  
