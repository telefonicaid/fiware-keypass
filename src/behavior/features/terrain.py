from lettuce import *
import requests
import json


tenantList = [ "511", "615", "634", "515" ]

def initialize():
  with open("properties.json") as config_file:
    world.config = json.load(config_file)

@before.each_scenario
def cleanContext(feature):
  for tenant in tenantList:
    url = world.config['targetUrl'] + '/pap/v1/' + tenant
    r = requests.delete(url)

initialize()      
