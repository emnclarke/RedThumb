#!/usr/bin/env python

import json, time

dataFile = open("data.txt", 'r')
data = dataFile.read()
dataFile.close()
timestamp = data[:15]
rawData = data[16:]

# Json validator
def isJson(myjson):
  try:
    jsonObject = json.loads(myjson)
  except ValueError:
    return False
  return True
  
print("Collecting data (takes two seconds)...\n")

dataFile = open("data.txt", 'r')
data = dataFile.read()
dataFile.close()

timestamp1 = data[:15]
dataString = data[16:]

time.sleep(2)

dataFile = open("data.txt", 'r')
data2 = dataFile.read()
dataFile.close()

timestamp2 = data2[:15]
dataString2 = data2[16:]

# Test 1
print ("Test 1, are timestamps updating (arduino is connected)")
if not timestamp1 == timestamp2:
    print ("pass")
else:
    print ("fail")

# Test 2
print ("Test 2, are both data points valid")
if isJson(dataString) and isJson(dataString2):
    print ("pass")
else:
    print ("fail")
    
rawData = dataString

print("Test 3: are all values present?")
jsonData = json.loads(rawData)
try:
    dataTuple = (jsonData["temperature"], jsonData["humidity"], str(jsonData["soilMoisture"]), int(jsonData["sunlight"]), int(jsonData["lowWater"]))
    print("pass")
except Exception:
    print("fail")

print("Test 4: is temperature in valid range? (0-50 C)")
if int(jsonData["temperature"]) >= 0 and int(jsonData["temperature"]) <= 50:
    print("pass")
else:
    print("fail")
    
print("Test 5: is humidity in valid range? (20-90%)")
if int(jsonData["humidity"]) >= 20 and int(jsonData["humidity"]) <= 90:
    print("pass")
else:
    print("fail")
    
print("Test 6: is soil moisture valid option")
acceptableValues = ["dry", "wet", "water"]
if str(jsonData["soilMoisture"]) in acceptableValues:
    print("pass")
else:
    print("fail")
    
print("Test 7: is sunlight a boolean")
if jsonData["sunlight"] == True or jsonData["sunlight"] == False:
    print("pass")
else:
    print("fail")
    
print("Test 8: is lowWater a boolean")
if jsonData["lowWater"] == True or jsonData["lowWater"] == False:
    print("pass")
else:
    print("fail")

