import socket
import sys
import json
from dataClasses import *
from DBManager import *
import logging

debug = True

logging.basicConfig(filename='appListener.log', filemode='w', format='%(levelname)s: %(message)s', level=logging.INFO)

hubIP = '10.0.0.71'

# Setting up UDP listener
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
port = 11616
server_address = (hubIP, port)
s.bind(server_address)

# Setting up database
redThumbdb = DBManager()

def makeJsonArray(dataList):
    array = "["
    for dataPoint in dataList:
        array += dataPoint.toJson() + ", "

    array = array[:-2] + "]"

    return array

while True:
    logging.info("Waiting to receive request from app at: " + str(hubIP) + " port " + str(port) + "\n")
    
    buf, address = s.recvfrom(port)
    if not len(buf):
        logging.error("Received empty data" )
    
    logging.info("Received request from app")
    
    if debug:
        logging.debug("request: " + buf)

    print(buf)

    appIP = buf.split()[0]
    app_address = (appIP, port)


    # Requests
    if buf.split()[1] == "requestPots":
        logging.info("Pots requested")
        data = redThumbdb.fetchPots()
        s.sendto(makeJsonArray(data), app_address)

    elif buf.split()[1] == "requestPlantType":
        type = buf.split()[2]
        logging.info("Plant type %s requested" % type)
        data = redThumbdb.fetchPlantType(int(type))
        s.sendto(data.toJson(), app_address)

    elif buf.split()[1] == "requestAllPlantTypes":
        logging.info("All plant types requested")
        data = redThumbdb.fetchPlantTypes()
        s.sendto(makeJsonArray(data), app_address)

    elif buf.split()[1] == "requestPotCurrentData":
        pot = buf.split()[2]
        logging.info("Pot %s current sensor data requested" % pot)
        data = redThumbdb.fetchCurrentData(int(pot))
        s.sendto(data.toJson(), app_address)

    elif buf.split()[1] == "requestPotRecentData":
        pot = buf.split()[2]
        points = buf.split()[3]
        offset = buf.split()[4]
        logging.info("Pot %s recent sensor data requested, %s data points" % (pot, points))
        data = redThumbdb.fetchOffsetData(int(pot), int(points), int(offset))
        print(makeJsonArray(data))
        s.sendto(makeJsonArray(data), app_address)

    elif buf.split()[1] == "requestCompleteDataPot":
        pot = buf.split()[2]
        logging.info("Pot %s all data requested" % pot)
        potData = redThumbdb.fetchPot(int(pot))
        plantTypeData = redThumbdb.fetchPlantType(potData.plantID)
        plantData = redThumbdb.fetchRecentData(int(pot), 60)
        message = "[" + potData.toJson() + ", " + plantTypeData.toJson() + ", " + makeJsonArray(plantData) + "]"
        s.sendto(message, app_address)

    # Data adjustments

    elif buf.split()[1] == "addPlantType":
        jsonData = json.loads(buf[23:])
        plantType = PlantType(999, jsonData["name"], jsonData["water_frequency"], jsonData["water_length"], jsonData["temperature"], jsonData["humidity"], jsonData["soil_moisture"], jsonData["sun_coverage"])
        redThumbdb.submitPlantType(plantType)
        s.sendto("ack", app_address)

    elif buf.split()[1] == "updatePlantType":
        jsonData = json.loads(buf[26:])
        logging.info("Update plant type %s" % str(jsonData["plant_id"]))
        plantType = PlantType(jsonData["plant_id"], jsonData["name"], jsonData["water_frequency"], jsonData["water_length"], jsonData["temperature"], jsonData["humidity"], jsonData["soil_moisture"], jsonData["sun_coverage"])
        redThumbdb.updatePlantType(plantType)
        s.sendto("ack", app_address)

    elif buf.split()[1] == "deletePlantType":
        type = buf.split()[2]
        logging.info("Delete plant type %s" % type)
        redThumbdb.deletePlantType(int(type))
        s.sendto("ack", app_address)

    elif buf.split()[1] == "addPot":
        jsonData = json.loads(buf[17:])
        smartPot = SmartPot(999, jsonData["name"], jsonData["pot_ip"], jsonData["plant_id"], jsonData["last_watered"], jsonData["low_water"], jsonData["water_flag"])
        redThumbdb.submitPot(smartPot)
        s.sendto("ack", app_address)
    
    elif buf.split()[1] == "updatePot":
        jsonData = json.loads(buf[20:])
        logging.info("Update plant type %s" % str(jsonData["pot_id"]))
        smartPot = SmartPot(jsonData["pot_id"], jsonData["name"], jsonData["pot_ip"], jsonData["plant_id"], jsonData["last_watered"], jsonData["low_water"], jsonData["water_flag"])
        redThumbdb.updatePot(smartPot)
        s.sendto("ack", app_address)

    elif buf.split()[1] == "deletePot":
        pot = buf.split()[2]
        logging.info("Delete pot %s" % pot)
        redThumbdb.deletePot(int(pot))
        s.sendto("ack", app_address)



# Example requests and responses

# "requestPots" - JSON object array with all pots
# [{"pot_id": x, "name": x, "pot_ip": x, "plant_id": x, "last_watered": x, "low_water": x, "water_flag": x}]

# "requestPlantType 1" - JSON object with specified plant type info
# {"plant_id": x, "name": x, "water_frequency": x, "water_length": x, "temperature": x, "humidity": x,"soil_moisture": x, "sun_coverage": x}

# "requestAllPlantTypes" - JSON object array with all plant types
# [{"plant_id": x, "name": x, "water_frequency": x, "water_length": x, "temperature": x, "humidity": x,"soil_moisture": x, "sun_coverage": x}]

# "requestPotCurrentData 1" - JSON object with current pot data of specified pot
# {"pot_id": x, "timestamp": x, "name": x, "temperature": x, "humidity": x,"soil_moisture": x, "sunlight": x}

# "requestPotRecentData 1 1" - JSON object array with most recent pot data of specified pot and specified number of data points
# [{"pot_id": x, "timestamp": x, "name": x, "temperature": x, "humidity": x,"soil_moisture": x, "sunlight": x}]

# "requestCompleteDataPot 1" - JSON object array with all pot, plant and data of specified pot, max 60 data points of pot (1 hour)
# [{"pot_id": x, "name": x, "pot_ip": x, "plant_id": x, "last_watered": x, "low_water": x, "water_flag": x}, {"plant_id": x, "name": x, "water_frequency": x, "water_length": x, "temperature": x, "humidity": x,"soil_moisture": x, "sun_coverage": x}, [{"pot_id": x, "timestamp": x, "name": x, "temperature": x, "humidity": x,"soil_moisture": x, "sunlight": x}]]

# Change pots/plant types (All get sent back an "ack"

# "addPlantType" - Adds plant type, example below. Any value that is null will go to mysql default value
# "10.0.0.70 addPlantType {"plant_id": null, "name": "example", "water_frequency": null, "water_length": null, "temperature": null, "humidity": null,"soil_moisture": null, "sun_coverage": null}"

# "updatePlantType" - Updates plant type, example below. Any value that different from what is in database will be changed. Null values are ignored
# "10.0.0.70 updatePlantType {"plant_id": null, "name": "example2", "water_frequency": 5, "water_length": null, "temperature": null, "humidity": null,"soil_moisture": null, "sun_coverage": null}"

# "deletePlantType 5" - Deletes specified plant type

# "addPot" - Adds pot, example below. Any value that is null will go to mysql default value
# "10.0.0.70 addPot {"pot_id": 5, "name": "aaabbba", "pot_ip": null, "plant_id": null, "last_watered": null, "low_water": null, "water_flag": null}"

# "updatePot" - Updates plant type, example below. Any value that different from what is in database will be changed. Null values are ignored
# "10.0.0.70 updatePot {"pot_id": 5, "name": "aaab123bba", "pot_ip": null, "plant_id": 2, "last_watered": null, "low_water": null, "water_flag": null}"
# "deletePot 5" - Deletes specified pot
