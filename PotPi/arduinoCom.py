#!/usr/bin/env python

import serial

debug = True

# Setting up serial connection
serialport = serial.Serial("/dev/ttyACM0", 9600, timeout=1)

data = ""
discard = 5 # Number of initial data points to discard (when system is starting up)

# Serial port read loop
while True:
    command = serialport.readline()
    if command != "":
        if debug:
            print ("DEBUG: Command: " + command.rstrip())
        splitCommand = command.split()
        if len(splitCommand) > 0:
            # Selecting just the data from the arduino command
            if splitCommand[0] == "reportSensorData" and discard <= 0:
                print("INFO: Sensor Data has been read")
                data = splitCommand[1]
                if debug:
                    print ("DEBUG: Data: " + data)
            # Discarding boot up data to allow sensors to properly adjust
            elif discard > 0:
                print("INFO: Discarding initial boot data")
                discard -= 1;
    else:
        print("ERROR: Read empty data from serial port")

    # If data is available, write to file
    if data != "":
        dataFile = open("data.txt", 'w+')
        dataFile.write(data)
        dataFile.close()
        print("INFO: Sensor Data has been writen to file")
