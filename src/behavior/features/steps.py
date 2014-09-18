from lettuce import *
from uuid import uuid4
import requests
from lxml import etree
import pystache

#TODO: Make the test parameters configurable.
TARGET_URL='http://localhost:8080'

@step('I send a policy creation request to the Access Control for tenant "([^"]*)" and subject "([^"]*)"')
def sendPolicyCreationRequest(step, tenant, subject):
  url = TARGET_URL + '/pap/v1/' + tenant + '/' + subject
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
  url = TARGET_URL + '/pdp/v3/' + tenant
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

