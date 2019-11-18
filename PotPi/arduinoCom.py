#!/usr/bin/env python

import serial
import json

serialport = serial.Serial("/dev/ttyACM0", 9600, timeout=1)

data = ""
discard = 3 # Number of initial data points to discard (when system is starting up)


while True:
    command = serialport.readline()
    if command != "":
        splitCommand = command.split()
        if len(splitCommand) > 0:
            if splitCommand[0] == "reportSensorData" and discard <= 0:
                data = json.loads(splitCommand[1])
            elif discard > 0:
                discard -= 1;

if data != "":
    dataFile = open("data.txt", 'w+')
    dataFile.write(json.dumps(data))
    dataFile.close()
