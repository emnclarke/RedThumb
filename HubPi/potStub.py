#!/usr/bin/env python

import socket, sys, time
import serial

debug = True

hubIP = '10.0.0.71'

# Setting up UDP listener
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
port = 1854
server_address = ('localhost', port)
s.bind(server_address)

# Requests Arduino triggers water pump to water plant by sending 'w' char
# each 'w' sent turns the pump on for one second
def waterPlant(length):
    print ("INFO: Water requested")
    message = ""
    for i in range(length):
        message += "w"
        
    if debug:
        print ("DEBUG: Water message: " + message)
    
    print("STUB: Simulated water plant message")
    s.sendto("WaterAck", (hubIP, port))
    print ("INFO: Plant watered")

while True:
    print ("\nINFO: Waiting to receive request from hub at: " + str(hubIP) + " port " + str(port) + "\n")
    
    buf, address = s.recvfrom(port)
    if not len(buf):
        print("ERROR: Received empty data" )
        break
    
    print ("INFO: Received request from hub")
    
    if debug:
        print ("DEBUG: request: " + buf)

    # Reads current sensor data from file and sends data to hub
    if buf.split()[0] == "RequestPotData":
        print("INFO: Data requested")
        dataFile = open("data.txt", 'r')
        data = dataFile.read()
        dataFile.close()
        message = "ReportPotData " + data[16:]
        
        if debug:
            print ("DEBUG: Data message: " + message)
        
        s.sendto(message, (hubIP, port))
        print ("INFO: Message sent to hub")
    elif buf.split()[0] == "WaterPlant":
        if len(buf.split()) == 1:
            length = 1
        else:
            length = int(buf.split()[1])
        waterPlant(length)
    else:
        print("ERROR: Unknown request: " + buf)

s.shutdown(1)

