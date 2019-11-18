#!/usr/bin/env python

import socket, sys, time
import serial

hubIP = '192.168.0.190'

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
port = 1854
server_address = ('192.168.0.191', port)
s.bind(server_address)

serialport = serial.Serial("/dev/ttyACM0", 9600, timeout=1)

def ReqPotData()
    print("INFO: Data has been requested")
    dataFile = open("data.txt", 'r')
    data = dataFile.read()
    dataFile.close()
    message = "ReportPotData " + data
    print ("Sending message: " + message)
    s.sendto(message, (hubIP, port))

def TakePhoto()
    print ("INFO: Picture taken")
    #ToDo: Take pictures with the pi
    #...
    s.sendto("PictureAck", (hubIP, port))

def WaterPlant()
    print ("INFO: Plant watered")
    serialport.write('w')
    s.sendto("WaterAck", (hubIP, port))


while True:
    print ("Waiting to receive on port %d : press Ctrl-C or Ctrl-Break to stop " % port)
    
    buf, address = s.recvfrom(port)
    if not len(buf):
        print("ERROR: Incorrectly recieved data" )
        break
    print ("Received %s bytes from %s %s: " % (len(buf), address, buf))
    print(buf)

    if buf.split()[0] == "RequestPotData":
        ReqPotData()
    elif buf.split()[0] == "TakePicture":
        TakePhoto()
    elif buf.split()[0] == "WaterPlant":
        WaterPlant()
    else
	print("ERROR: Trouble reading request")

s.shutdown(1)

