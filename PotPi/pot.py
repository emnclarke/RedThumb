#!/usr/bin/env python

import socket, sys, time
import serial
import logging

debug = True
testingMode = False
onMac = False

logging.basicConfig(filename='pot.log', filemode='w', format='%(levelname)s: %(message)s', level=logging.INFO)


hubIP = '10.0.0.71'

if testingMode:
    hubIP = 'localhost'

# Setting up UDP listener
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
port = 1854
server_address = ('10.0.0.72', port)
s.bind(server_address)
try:
    if onMac:
        # Setting up serial connection
        serialport = serial.Serial("/dev/cu.usbmodem142401", 9600, timeout=1)
    else:
        # Setting up serial connection
        serialport = serial.Serial("/dev/ttyACM0", 9600, timeout=1)
except:
    logging.error("Arduino likely not connected")

# Requests Arduino triggers water pump to water plant by sending 'w' char
# each 'w' sent turns the pump on for one second
def waterPlant(length):
    logging.info("Water requested")
    message = ""
    for i in range(length):
        message += "w"
        
    if debug:
        logging.debug("Water message: " + message)
    
    try:
        serialport.write(message)
    except NameError as e:
        logging.error("Serial connection does not exist")
    s.sendto("WaterAck", (hubIP, port))
    logging.info("Plant watered")


lastDataTime = None
while True:
    logging.info("Waiting to receive request from hub at: " + str(hubIP) + " port " + str(port) + "\n")
    
    buf, address = s.recvfrom(port)
    if not len(buf):
        logging.error("Received empty data" )
        break
    
    logging.info("Received request from hub")
    
    if debug:
        logging.debug("request: " + buf)

    # Reads current sensor data from file and sends data to hub
    if buf.split()[0] == "RequestPotData":
        logging.info("Data requested")
        dataFile = open("data.txt", 'r')
        data = dataFile.read()
        dataFile.close()
        timestamp = data[:15]
        if lastDataTime is not None and lastDataTime == timestamp:
            message = "ArduinoError"
            logging.error("Arduino not updating sensor data")
        else:
            message = "ReportPotData " + data[16:]
            lastDataTime = timestamp
        
        if debug:
            logging.debug("Data message: " + message)
        
        s.sendto(message, (hubIP, port))
        logging.info("Message sent to hub")
    elif buf.split()[0] == "WaterPlant":
        if len(buf.split()) == 1:
            length = 1
        else:
            length = int(buf.split()[1])
        waterPlant(length)
    else:
        logging.error("Unknown request: " + buf)

s.shutdown(1)

