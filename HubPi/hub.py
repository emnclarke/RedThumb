#!/usr/bin/env python

import socket, sys, time, datetime
import json
import mysql.connector

starttime=time.time()
dataCollectionInterval = 10.0

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.settimeout(1)
port = 1854
server_address = ('192.168.0.190', port)
s.bind(server_address)

redThumbdb = mysql.connector.connect(
    host="localhost",
    user="python",
    passwd="python",
    database="red_thumb"
)

dbcursor = redThumbdb.cursor()

pictureFlag = False
waterFlag = False

while True:
    dbcursor.execute("SELECT * FROM SmartPots")
    pots = dbcursor.fetchall()
    for pot in pots:
        potID = pot[0]
        potIP = pot[2]
        plantType = pot[3]
        lastWatered = pot[4]
        lowWaterFlag = pot[5]
        dbWaterFlag = pot[6]
        s.sendto("RequestPotData", (potIP, port))
        print ("Waiting to receive data from " + potIP)
        try:
            buf, address = s.recvfrom(port)
            if buf.split()[0] != "ReportPotData":
                print ("Received wrong response")
            else:
                data = buf[14:]
                print ("dumping to db: " + data)
                jsonData = json.loads(data)
                sql = "INSERT INTO PlantData(pot_id, temperature, humidity, soil_moisture, sunlight) VALUES (%d,%f,%f,\"%s\",%d)"
                val = (potID, jsonData["temperature"], jsonData["humidity"], jsonData["soilMoisture"], int(jsonData["sunlight"]))
                sqlStatement = sql % val
                print sqlStatement
                dbcursor.execute(sqlStatement)
                redThumbdb.commit()
            
                if lastWatered is None and jsonData["soilMoisture"] == "dry":
                    waterFlag = True
            
                if jsonData["lowWater"]:
                    dbcursor.execute("UPDATE SmartPots SET low_water=1 WHERE pot_id=" + str(potID))
                else:
                    dbcursor.execute("UPDATE SmartPots SET low_water=0 WHERE pot_id=" + str(potID))

            dbcursor.execute("SELECT * FROM PlantTypes WHERE plant_type=" + str(plantType))
            plantInfo = dbcursor.fetchone()
            waterFrequency = plantInfo[2]
            waterLength = plantInfo[3]
            waterDelta = 0
            
            if lastWatered is not None:
                waterDelta = (datetime.date.today() - lastWatered).days
            
            if dbWaterFlag or waterDelta >= waterFrequency:
                waterFlag = True

            if pictureFlag:
                print ("taking picture")
                s.sendto("TakePicture", (potIP, port))
                print ("Waiting to receive response from " + potIP)
                buf, address = s.recvfrom(port)
                if buf.split()[0] != "pictureAck":
                    print ("Received wrong response")
                else:
                    print ("picture taken")
                    pictureFlag = False

            if waterFlag and not lowWaterFlag:
                print ("watering plant")
                s.sendto("WaterPlant", (potIP, port))
                print ("Waiting to receive response from " + potIP)
                buf, address = s.recvfrom(port)
                if buf.split()[0] != "WaterAck":
                    print ("Received wrong response")
                else:
                    print ("plant watered")
                    dbcursor.execute("UPDATE SmartPots SET last_watered=CURRENT_DATE, water_flag=0 WHERE pot_id=" + str(potID))
                    waterFlag = False
    
        except socket.timeout:
            print ("Pot " + str(potID) + " timeout")

    time.sleep(dataCollectionInterval - ((time.time() - starttime) % dataCollectionInterval))

quit()
