#!/usr/bin/env python

import socket, sys, time, datetime
import json
import mysql.connector

debug = False

# Starting variables to have data collection run every x seconds
starttime=time.time()
dataCollectionInterval = 10.0

# Setting up UDP listener
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.settimeout(1)
port = 1854
server_address = ('192.168.0.190', port)
s.bind(server_address)

# Connecting to database
redThumbdb = mysql.connector.connect(
    host="localhost",
    user="python",
    passwd="python",
    database="red_thumb"
)
dbcursor = redThumbdb.cursor()

# Local flags
pictureFlag = False
waterFlag = False


def collectData():
    print ("INFO: Data received from pot " + str(potID) + " at ip: " + potIP)
    print ("INFO: Submitting sensor data to Database")
    data = buf[14:]
    
    if debug:
        print ("DEBUG: Pot " + str(potID) + " sensor data: " + data.rstrip())
            
    jsonData = json.loads(data)
    sql = "INSERT INTO PlantData(pot_id, temperature, humidity, soil_moisture, sunlight) VALUES (%d,%f,%f,\"%s\",%d)"
    val = (potID, jsonData["temperature"], jsonData["humidity"], jsonData["soilMoisture"], int(jsonData["sunlight"]))
    sqlStatement = sql % val
    
    if debug:
        print ("DEBUG: Sql statement: " + sqlStatement)
    
    dbcursor.execute(sqlStatement)
    redThumbdb.commit()

    # If the plant has never been watered before and soil is currently dry, triggers first watering
    # Prevents unnecesary first watering
    if lastWatered is None and jsonData["soilMoisture"] == "dry":
        print ("INFO: Plant has never been watered and soil is dry, setting waterFlag to True")
        waterFlag = True

    # Updates database water flag based on water sensor
    if jsonData["lowWater"]:
        print ("INFO: Low water, updating Database")
        dbcursor.execute("UPDATE SmartPots SET low_water=1 WHERE pot_id=" + str(potID))
    # Only disables low water flag if water flag is currently enabled, prevents unnecesary database actions
    elif lowWaterFlag:
        print ("INFO: Low water fixed, updating database")
        dbcursor.execute("UPDATE SmartPots SET low_water=0 WHERE pot_id=" + str(potID))


def takePicture():
    print ("INFO: Requesting pot " + str(potID) + " take picture")
    s.sendto("TakePicture", (potIP, port))
    print ("INFO: Waiting to receive pictureAck from pot " + str(potID) + " at ip: " + potIP)
    buf, address = s.recvfrom(port)
    if buf.split()[0] != "pictureAck":
        print ("ERROR: Received wrong response from pot " + str(potID) + " at ip: " + potIP)
    else:
        print ("INFO: pictureAck received from pot " + str(potID) + " at ip: " + potIP)
        pictureFlag = False


def waterPlant():
    print ("INFO: Requesting pot " + str(potID) + " water plant")
    s.sendto("WaterPlant", (potIP, port))
    print ("INFO: Waiting to receive waterAck from pot " + str(potID) + " at ip: " + potIP)
    buf, address = s.recvfrom(port)
    if buf.split()[0] != "WaterAck":
        print ("ERROR: Received wrong response from pot " + str(potID) + " at ip: " + potIP)
    else:
        print ("INFO: waterAck received from pot " + str(potID) + " at ip: " + potIP + ". Updating database to disable waterFlag")
        dbcursor.execute("UPDATE SmartPots SET last_watered=CURRENT_DATE, water_flag=0 WHERE pot_id=" + str(potID))
        waterFlag = False


# data collection loop
while True:
    # Fetching all pots
    print ("INFO: Fetching Smart Pots from Database")
    dbcursor.execute("SELECT * FROM SmartPots")
    pots = dbcursor.fetchall()
    
    # Data collection for each pot
    for pot in pots:
        # Converting pot info to readable variables
        potID = pot[0]
        potIP = pot[2]
        plantType = pot[3]
        lastWatered = pot[4]
        lowWaterFlag = pot[5]
        dbWaterFlag = pot[6]
        
        print("\nINFO: Pot " + str(potID))
        
        if debug:
            print ("DEBUG: Pot " + str(potID) + " data: " + str(pot))
        
        # Requesting data from pot
        print ("INFO: Requesting info from pot " + str(potID) + " at ip: " + potIP)
        s.sendto("RequestPotData", (potIP, port))
        
        # Attempting to receive data
        try:
            print ("INFO: Waiting to receive data from pot " + str(potID) + " at ip: " + potIP)
            buf, address = s.recvfrom(port)
            if buf.split()[0] != "ReportPotData":
                print ("ERROR: Received wrong response from pot " + str(potID) + " at ip: " + potIP)
            else:
                # Collect data if available
                collectData()

            # Get plant type info for automatic watering
            print ("INFO: Fetching plant type data from Database")
            dbcursor.execute("SELECT * FROM PlantTypes WHERE plant_type=" + str(plantType))
            plantInfo = dbcursor.fetchone()
            waterFrequency = plantInfo[2]
            waterLength = plantInfo[3]
            waterDelta = 0
            
            if debug:
                print ("DEBUG: Plant " + str(plantType) + " info: " + str(plantInfo))
            
            if lastWatered is not None:
                waterDelta = (datetime.date.today() - lastWatered).days
                if debug:
                    print ("DEBUG: Water delta: " + str(waterDelta))
            
            if dbWaterFlag:
                waterFlag = True
                if debug:
                    print ("DEBUG: Water flag triggered by database")
            elif waterDelta >= waterFrequency:
                waterFlag = True
                if debug:
                    print ("DEBUG: Water flag triggered by water delta")

            if pictureFlag:
                takePicture()

            if waterFlag and not lowWaterFlag:
                waterPlant()
            
        # Pot did not reply to data request, timeout
        except socket.timeout:
            print ("ERROR: Pot " + str(potID) + " timeout at ip: " + potIP + ". Continuing to next pot")
    
    # Sleep statment that ensures data collection happens every x seconds on the dot, prevents drifting
    print ("\nINFO: Waiting for next data collection. Current interval: " + str(dataCollectionInterval) + "s\n")
    time.sleep(dataCollectionInterval - ((time.time() - starttime) % dataCollectionInterval))

quit()
