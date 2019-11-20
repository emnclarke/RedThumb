#!/usr/bin/env python

import socket, sys, time, datetime
import json

# Setting up UDP listener
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.settimeout(1)
port = 1854
server_address = ('localhost', port)
s.bind(server_address)

potIP = "192.168.0.191"

# Json validator
def isJson(myjson):
  try:
    jsonObject = json.loads(myjson)
  except ValueError:
    return False
  return True
  
# Test 1
print("Test 1: RequestPotData")

# Writing to file
print("Writing example data to data.txt")
dataFile = open("data.txt", 'w+')
timestamp = str(time.time()) + ""
while len(timestamp) < 15:
    timestamp += "0"
stubData = "{\"lowWater\": false, \"soilMoisture\": \"dry\", \"temperature\": 21.7, \"sunlight\": true, \"humidity\": 21.0}"
dataFile.write(timestamp + " " + stubData)
dataFile.close()

s.sendto("RequestPotData", (potIP, port))
buf, address = s.recvfrom(port)

print("Test 1.1: Does received message start with: \"ReportPotData\"")
if buf.split()[0] == "ReportPotData":
    print("pass")
else:
    print("fail")
    
print("Test 1.2: is \"ReportPotData\" followed by valid json")
rawData = buf[14:]

if isJson(rawData):
    print("pass")
else:
    print("fail")
    

# Test 2
print("Test 2: RequestPotData with arduino not connected (not updating data.txt")
s.sendto("RequestPotData", (potIP, port))
buf, address = s.recvfrom(port)

print("Expecting \"ArduinoError\"")
if buf == "ArduinoError":
    print("pass")
else:
    print("fail")
    

# Test 3
print("Test 3: WaterPlant")
s.sendto("WaterPlant 1", (potIP, port))
buf, address = s.recvfrom(port)

if buf == "WaterAck":
  print("pass")
else:
  print("fail")
  
# Test 4
print("Test 4: WaterPlant without specifying legnth")
s.sendto("WaterPlant", (potIP, port))
buf, address = s.recvfrom(port)

if buf == "WaterAck":
  print("pass")
else:
  print("fail")
