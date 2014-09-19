from lettuce import *
import requests

TARGET_URL='http://localhost:8080'

tenantList = [ "511", "615", "634", "515" ]

@before.each_scenario
def cleanContext(feature):
  for tenant in tenantList:
    url = TARGET_URL + '/pap/v1/' + tenant
    r = requests.delete(url)

      
