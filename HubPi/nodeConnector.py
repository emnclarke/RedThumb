import socket
import sys
import json
from dataClasses import *
from DBManager import *
import logging

requestMessage = sys.argv[1]
arg1 = ""
arg2 = ""
arg3 = ""
arg4 = ""
arg5 = ""
arg6 = ""
arg7 = ""
arg8 = ""

if len(sys.argv) >= 3:
    arg1 = sys.argv[2]
if len(sys.argv) >= 4:
    arg2 = sys.argv[3]
if len(sys.argv) >= 5:
    arg3 = sys.argv[4]
if len(sys.argv) >= 6:
    arg4 = sys.argv[5]
if len(sys.argv) >= 7:
    arg5 = sys.argv[6]
if len(sys.argv) >= 8:
    arg6 = sys.argv[7]
if len(sys.argv) >= 9:
    arg7 = sys.argv[8]
if len(sys.argv) >= 10:
    arg8 = sys.argv[9]
##### EXPAND THESE TO 8 ARGS (>= 10) #####

debug = True

logging.basicConfig(filename='nodeConnector.log', filemode='w', format='%(levelname)s: %(message)s', level=logging.INFO)

# Setting up database
redThumbdb = DBManager()

def makeJsonArray(dataList):
    array = "["
    for dataPoint in dataList:
        array += dataPoint.toJson() + ", "

    array = array[:-2] + "]"

    return array


# Requests
if requestMessage == "requestPots":
    logging.info("Pots requested")
    data = redThumbdb.fetchPots()
    print(makeJsonArray(data))

elif requestMessage == "requestPlantType":
    type = arg1
    logging.info("Plant type %s requested" % type)
    data = redThumbdb.fetchPlantType(int(type))
    print(data.toJson())

elif requestMessage == "requestAllPlantTypes":
    logging.info("All plant types requested")
    data = redThumbdb.fetchPlantTypes()
    print(makeJsonArray(data))

elif requestMessage == "requestPotCurrentData":
    pot = arg1
    logging.info("Pot %s current sensor data requested" % pot)
    data = redThumbdb.fetchCurrentData(int(pot))
    print(data.toJson())

elif requestMessage == "requestPotRecentData":
    pot = arg1
    points = arg2
    offset = arg3
    logging.info("Pot %s recent sensor data requested, %s data points" % (pot, points))
    data = redThumbdb.fetchOffsetData(int(pot), int(points), int(offset))
    print(makeJsonArray(data))

elif requestMessage == "requestCompleteDataPot":
    pot = arg1
    logging.info("Pot %s all data requested" % pot)
    potData = redThumbdb.fetchPot(int(pot))
    plantTypeData = redThumbdb.fetchPlantType(potData.plantID)
    plantData = redThumbdb.fetchRecentData(int(pot), 1440)
    message = "[" + potData.toJson() + ", " + plantTypeData.toJson() + ", " + makeJsonArray(plantData) + "]"
    print(message)

# Data adjustments

elif requestMessage == "addPlantType":
    jsonData = json.loads(arg1) ### Delete this, replace all jsonData with arg1 ... arg8
    plantType = PlantType(999, jsonData["name"], jsonData["water_frequency"], jsonData["water_length"], jsonData["temperature"], jsonData["humidity"], jsonData["soil_moisture"], jsonData["sun_coverage"])
    redThumbdb.submitPlantType(plantType)
    print("ack")

elif requestMessage == "updatePlantType":
    jsonData = json.loads(arg1)
    logging.info("Update plant type %s" % str(jsonData["plant_id"]))
    plantType = PlantType(jsonData["plant_id"], jsonData["name"], jsonData["water_frequency"], jsonData["water_length"], jsonData["temperature"], jsonData["humidity"], jsonData["soil_moisture"], jsonData["sun_coverage"])
    redThumbdb.updatePlantType(plantType)
    print("ack")

elif requestMessage == "deletePlantType":
    type = arg1
    logging.info("Delete plant type %s" % type)
    redThumbdb.deletePlantType(int(type))
    print("ack")

elif requestMessage == "addPot":
    jsonData = json.loads(arg1)
    smartPot = SmartPot(999, jsonData["name"], jsonData["pot_ip"], jsonData["plant_id"], jsonData["last_watered"], jsonData["low_water"], jsonData["water_flag"])
    redThumbdb.submitPot(smartPot)
    print("ack")

elif requestMessage == "updatePot":
    jsonData = json.loads(arg1)
    logging.info("Update plant type %s" % str(jsonData["pot_id"]))
    smartPot = SmartPot(jsonData["pot_id"], jsonData["name"], jsonData["pot_ip"], jsonData["plant_id"], jsonData["last_watered"], jsonData["low_water"], jsonData["water_flag"])
    redThumbdb.updatePot(smartPot)
    print("ack")

elif requestMessage == "deletePot":
    pot =json.loads(arg1)
    logging.info("Delete pot %s" % pot)
    redThumbdb.deletePot(int(pot))
    print("ack")
    
print ("end")



# Example requests and responses

# "requestPots" - JSON object array with all pots
# [{"pot_id": x, "name": x, "pot_ip": x, "plant_id": x, "last_watered": x, "low_water": x, "water_flag": x}]

# "requestPlantType 1" - JSON object with specified plant type info
# {"plant_id": x, "name": x, "water_frequency": x, "water_length": x, "temperature": x, "humidity": x,"soil_moisture": x, "sun_coverage": x}

# "requestAllPlantTypes" - JSON object array with all plant types
# [{"plant_id": x, "name": x, "water_frequency": x, "water_length": x, "temperature": x, "humidity": x,"soil_moisture": x, "sun_coverage": x}]

# "requestPotCurrentData 1" - JSON object with current pot data of specified pot
# {"pot_id": x, "timestamp": x, "name": x, "temperature": x, "humidity": x,"soil_moisture": x, "sunlight": x}

# "requestPotRecentData 1 1 1" - JSON object array with most recent pot data of specified pot and specified number of data points
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
