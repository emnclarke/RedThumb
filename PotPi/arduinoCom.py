#!/usr/bin/env python

import serial, time

debug = True
onMac = True


if onMac:
    # Setting up serial connection
    serialport = serial.Serial("/dev/cu.usbmodem142401", 9600, timeout=1)
else:
    # Setting up serial connection
    serialport = serial.Serial("/dev/ttyACM0", 9600, timeout=1)

data = ""
discard = 5 # Number of initial data points to discard (when system is starting up)

# Serial port read loop
while True:
    command = serialport.readline()
    if command != "":
        if debug:
            print ("DEBUG: Command: " + str(command.rstrip()))
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
        timestamp = str(time.time()) + ""
        while len(timestamp) < 15:
            timestamp += "0"
        dataFile.write(timestamp + " " + data)
        dataFile.close()
        print("INFO: Sensor Data has been writen to file")
