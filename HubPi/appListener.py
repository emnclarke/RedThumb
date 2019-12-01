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


# Example requests and responses

# "requestPots" - JSON object array with all pots
# [{"pot_id": x, "name": x, "plant_id": x, "last_watered": x, "low_water": x}]

# "requestPlantType 1" - JSON object with specified plant type info
# {"plant_id": x, "name": x, "water_frequency": x, "water_length": x, "temperature": x, "humidity": x,"soil_moisture": x, "sun_coverage": x}

# "requestAllPlantTypes" - JSON object array with all plant types
# [{"plant_id": x, "name": x, "water_frequency": x, "water_length": x, "temperature": x, "humidity": x,"soil_moisture": x, "sun_coverage": x}]

# "requestPotCurrentData 1" - JSON object with current pot data of specified pot
# {"pot_id": x, "timestamp": x, "name": x, "temperature": x, "humidity": x,"soil_moisture": x, "sunlight": x}

# "requestPotRecentData 1 1" - JSON object array with most recent pot data of specified pot and specified number of data points
# [{"pot_id": x, "timestamp": x, "name": x, "temperature": x, "humidity": x,"soil_moisture": x, "sunlight": x}]

# "requestCompleteDataPot 1" - JSON object array with all pot, plant and data of specified pot, max 60 data points of pot (1 hour)
# [{"pot_id": x, "name": x, "plant_id": x, "last_watered": x, "low_water": x}, {"plant_id": x, "name": x, "water_frequency": x, "water_length": x, "temperature": x, "humidity": x,"soil_moisture": x, "sun_coverage": x}, [{"pot_id": x, "timestamp": x, "name": x, "temperature": x, "humidity": x,"soil_moisture": x, "sunlight": x}]]
