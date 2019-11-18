#!/usr/bin/env python

import socket, sys, time

hubIP = '192.168.0.190'

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
port = 1854
server_address = ('localhost', port)
s.bind(server_address)

while True:
    print ("Waiting to receive on port %d : press Ctrl-C or Ctrl-Break to stop " % port)
    
    buf, address = s.recvfrom(port)
    if not len(buf):
        break
    print ("Received %s bytes from %s %s: " % (len(buf), address, buf))
    print(buf)

    if buf.split()[0] == "RequestPotData":
        dataFile = open("data.txt", 'r')
        data = dataFile.read()
        dataFile.close()
        message = "ReportPotData " + data
        print ("Sending message: " + message)
        s.sendto(message, (hubIP, port))
    elif buf.split()[0] == "TakePicture":
        print ("Say cheese")
        s.sendto("PictureAck", (hubIP, port))
    elif buf.split()[0] == "WaterPlant":
        print ("Sending pump signal")
        s.sendto("WaterAck", (hubIP, port))

s.shutdown(1)

