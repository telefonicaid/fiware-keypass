from lettuce import *
from uuid import uuid4
import requests
from lxml import etree
import pystache

@step('I send a policy creation request to the Access Control for tenant "([^"]*)" and subject "([^"]*)"')
def sendPolicyCreationRequest(step, tenant, subject):
  url = world.config['targetUrl'] + '/pap/v1/' + tenant + '/subject/' + subject
  fRequest = open('./requests/policy.xml', 'r')
  payload = pystache.render(fRequest.read(), {'ruleId': str(uuid4())})
  headers = {'content-type': 'application/xml'}
  r = requests.post(url, data=payload, headers=headers)
  world.retrievedRequest = r

@step('the Access Control returns a 201 OK and a payload with the ID')
def checkAccessControlReturnsOkAndId(step):
  assert world.retrievedRequest.status_code == 201

@step('I can retrieve the created policy from the Access Control')
def retrieveCreatedPolicy(step):
  location = world.retrievedRequest.headers.get('Location')
  r = requests.get(location)
  assert r.status_code == 200
  
@step('I send a validation request for tenant "([^"]*)" with subject "([^"]*)", FRN "([^"]*)" and action "([^"]*)"')
def sendValidationRequest(step, tenant, subject, frn, action):
  url = world.config['targetUrl'] + '/pdp/v3/' + tenant
  fRequest = open('./requests/request.xml', 'r')
  payload = pystache.render(fRequest.read(), {'subject' : subject, 'frn': frn, 'action': action}) 
  headers = {'content-type': 'application/xml'}
  r = requests.post(url, data=payload, headers=headers)
  world.retrievedRequest = r

@step('Then the Access Control should "([^"]*)" the access')
def accessControlDecidesAction(step, decision):
  assert world.retrievedRequest.status_code == 200
  requestXML = etree.XML(world.retrievedRequest.text)
  decisionAC = requestXML.xpath('//t:Decision', namespaces={'t': 'urn:oasis:names:tc:xacml:3.0:core:schema:wd-17'})
  assert len(decisionAC) == 1
  assert decisionAC[0].text == decision

@step('I send a remove request for the last request')
def removeLastRequest(step):
  location = world.retrievedRequest.headers.get('Location')
  r = requests.delete(location)
  world.removalResult = r

@step('I send a remove request for tenant "([^"]*)"')
def removeTenant(step, tenant):
  url = world.config['targetUrl'] + '/pap/v1/' + tenant
  r = requests.delete(url)
  world.removalResult = r

@step('I send a remove request for subject "([^"]*)" in tenant "([^"]*)"')
def removeSubject(step, subject, tenant):
  url = world.config['targetUrl'] + '/pap/v1/' + tenant + '/subject/' + subject
  r = requests.delete(url)
  world.removalResult = r

@step('I get the list of policies for the tenant "([^"]*)" and subject "([^"]*)"')
def getTenantPolicies(step, tenant, subject):
  url = world.config['targetUrl'] + '/pap/v1/' + tenant + '/subject/' + subject
  r = requests.get(url)
  world.requestList = r    

@step('trying to get the policy raises a 404')
def getLastPolicy(step):
  location = world.retrievedRequest.headers.get('Location')
  r = requests.get(location)
  assert r.status_code == 404

@step('I send the following policies to the access control')
def sendPoliciesToAccessControl(step):
  for policy in step.hashes:
    tenant = policy['tenant']
    subject = policy['subject']
    url = world.config['targetUrl'] + '/pap/v1/' + tenant + '/subject/' + subject
    fRequest = open('./requests/policy.xml', 'r')
    payload = pystache.render(fRequest.read(), {'ruleId': policy['id']})
    headers = {'content-type': 'application/xml'}
    r = requests.post(url, data=payload, headers=headers)

@step('the number of policies in the list is "([^"]*)"')
def testNumberOfPolicies(step, number):
  assert world.requestList.status_code == 200
  listXML = etree.XML(world.requestList.text)
  policies = listXML.xpath('//t:Policy', namespaces={'t': 'urn:oasis:names:tc:xacml:3.0:core:schema:wd-17'})
  assert len(policies) == int(number)


