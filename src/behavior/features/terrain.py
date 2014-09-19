from lettuce import *
import requests
import json
import os


tenantList = [ "511", "615", "634", "515" ]

def initialize():
  if os.getenv("LETTUCE_CONFIG"):
    filename = os.getenv("LETTUCE_CONFIG")
  else:
    filename = "properties.json"

  with open(filename) as config_file:
    world.config = json.load(config_file)

@before.each_scenario
def cleanContext(feature):
  for tenant in tenantList:
    url = world.config['targetUrl'] + '/pap/v1/' + tenant
    r = requests.delete(url)

initialize()      
