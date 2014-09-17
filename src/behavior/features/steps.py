from lettuce import *
import requests
from lxml import etree

TARGET_URL='http://localhost:8080'

@step('I send a policy creation request to the Access Control')
def sendPolicyCreationRequest(step):
  tenant = '511'
  subject = '8194'
  url = TARGET_URL + '/pap/v1/' + tenant + '/' + subject
  fRequest = open('./requests/policy.xml', 'r')
  payload = fRequest.read()
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
  
