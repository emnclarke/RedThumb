#!/usr/bin/env python

import socket, sys, time, datetime
import json
from redThumbDataClasses import *
from DBManager import *

debug = True

# Starting variables to have data collection run every x seconds
starttime=time.time()
dataCollectionInterval = 10.0

# Setting up UDP listener
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.settimeout(1)
port = 1854
server_address = ('192.168.0.190', port)
s.bind(server_address)

# Setting up database
redThumbdb = DBManager()

# Local flags
pictureFlag = False
waterFlag = False

# Json validator
def isJson(myjson):
  try:
    jsonObject = json.loads(myjson)
  except ValueError:
    return False
  return True
  
# data collection loop
while True:
    # Fetching all pots
    print ("INFO: Fetching Smart Pots from Database")
    pots = redThumbdb.fetchPots()
    
    if len(pots) == 0:
        print ("ERROR: No pots found")
    
    # Data collection for each pot
    for pot in pots:
        print("\nINFO: Pot " + str(pot.potID))
        
        if debug:
            print ("DEBUG: Pot " + str(pot.potID) + " data: " + str(pot.toString()))
        
        # Requesting data from pot
        print ("INFO: Requesting data from pot " + str(pot.potID) + " at ip: " + pot.potIP)
        s.sendto("RequestPotData", (pot.potIP, port))
        
        # Attempting to receive data
        try:
            print ("INFO: Waiting to receive data from pot " + str(pot.potID) + " at ip: " + pot.potIP)
            buf, address = s.recvfrom(port)
            if buf.split()[0] != "ReportPotData":
                print ("ERROR: Received invalid data collection response from pot " + str(pot.potID) + " at ip: " + pot.potIP)
            else:
                # Collect data if available
                print ("INFO: Data received from pot " + str(pot.potID) + " at ip: " + pot.potIP)
                print ("INFO: Submitting sensor data to Database")
                rawData = buf[14:]
                
                if debug:
                    print ("DEBUG: Pot " + str(pot.potID) + " sensor data: " + rawData.rstrip())
                
                # Check if data is in valid json format, errors if not
                if not isJson(rawData):
                    print ("ERROR: Json object not valid, data not submitted to database")
                else:
                    jsonData = json.loads(rawData)
                    plantData = PlantData(None, pot.potID, jsonData["temperature"], jsonData["humidity"], jsonData["soilMoisture"], int(jsonData["sunlight"]))
                    
                    redThumbdb.submitPlantData(plantData)

                    # If the plant has never been watered before and soil is currently dry, triggers first watering
                    # Prevents unnecesary first watering
                    if pot.lastWatered is None and plantData.soilMoisture == "dry":
                        print ("INFO: Plant has never been watered and soil is dry, setting waterFlag to True")
                        pot.waterFlag = 1
                        redThumbdb.updatePot()

                    # Updates database water flag based on water sensor
                    if jsonData["lowWater"] and not pot.lowWater:
                        print ("INFO: Low water, updating Database")
                        pot.lowWater = 1
                        redThumbdb.updatePot(pot)
                    # Only disables low water flag if water flag is currently enabled, prevents unnecesary database actions
                    elif not jsonData["lowWater"] and pot.lowWater:
                        print ("INFO: Low water fixed, updating database")
                        pot.lowWater = 0
                        redThumbdb.updatePot(pot)

            # Get plant type info for automatic watering
            print ("INFO: Fetching plant type data from Database")
            plantInfo = redThumbdb.fetchPlantType(pot.plantID)
            
            if plantInfo is None:
                print ("ERROR: Plant type id " + str(pot.plantID) + " not found")
            else:
                waterDelta = 0
                
                if debug:
                    print ("DEBUG: Plant " + str(pot.plantID) + " info: " + plantInfo.toString())
                
                if pot.lastWatered is not None:
                    waterDelta = (datetime.date.today() - pot.lastWatered).days
                    if debug:
                        print ("DEBUG: Water delta: " + str(waterDelta))
                
                if pot.waterFlag:
                    waterFlag = True
                    if debug:
                        print ("DEBUG: Water flag triggered by database")
                elif waterDelta >= plantInfo.waterFrequency:
                    waterFlag = True
                    if debug:
                        print ("DEBUG: Water flag triggered by water delta")

                if waterFlag:
                    if pot.lowWater:
                        print ("ERROR: Can't water plant, water level low")
                    # Requests the plant be watered for "length" seconds
                    else:
                        print ("INFO: Requesting pot " + str(pot.potID) + " water plant")
                        message = "WaterPlant " + str(plantInfo.waterLength)
                        s.sendto(message, (pot.potIP, port))
                        print ("INFO: Waiting to receive waterAck from pot " + str(pot.potID) + " at ip: " + pot.potIP)
                        buf, address = s.recvfrom(port)
                        if buf.split()[0] != "WaterAck":
                            print ("ERROR: Received invalid water response from pot " + str(pot.potID) + " at ip: " + pot.potIP)
                        else:
                            print ("INFO: waterAck received from pot " + str(pot.potID) + " at ip: " + pot.potIP + ". Updating database to disable waterFlag")
                            pot.waterFlag = 0
                            redThumbdb.updatePot(pot)
                            waterFlag = False
            
        # Pot did not reply to data request, timeout
        except socket.timeout:
            print ("ERROR: Pot " + str(pot.potID) + " timeout at ip: " + pot.potIP + ". Continuing to next pot")
        except:
            print ("ERROR: Unhandled exception: " + str(sys.exc_info()[0]))
    
    # Sleep statment that ensures data collection happens every x seconds on the dot, prevents drifting
    print ("\nINFO: Waiting for next data collection. Current interval: " + str(dataCollectionInterval) + "s\n")
    time.sleep(dataCollectionInterval - ((time.time() - starttime) % dataCollectionInterval))

quit()

