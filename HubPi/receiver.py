import socket
import sys
import json

'''
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind(("192.168.2.18", 1234))
s.listen(5)

# Test Receiver
while True:
    clientsocket, address = s.accept()
    print(f"Connection from {address} has been established")
    clientsocket.send(bytes("Welcome to the server!", "utf-8"))
    clientsocket.close()

'''

# socket.socket: must use to create a socket.
# socket.AF_INET: Address Format, Internet = IP Addresses.
# socket.SOCK_STREAM: two-way, connection-based byte streams.
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# HOST = "socket.gethostname()"  # this is your localhost
HOST = "192.168.2.18"
PORT = 1234
print('Socket Created')

# Bind socket to Host and Port
try:
    s.bind((HOST, PORT))
except socket.error as err:
    print('Bind Failed, Error Code: ' + str(err[0]) + ', Message: ' + err[1])
    sys.exit()

print('Socket Bind Success!')

# listen(): This method sets up and start TCP listener.
s.listen(10)
print('Socket is now listening')

while True:
    # write code here to accept JSON Objects
    conn, addr = s.accept()
    print('Connect with ' + addr[0] + ':' + str(addr[1]))
    buf = conn.recv(64)
    print(buf)

s.close()
