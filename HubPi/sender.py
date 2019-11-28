import socket
import sys

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# socket.gethostname() should be the IP address of the server
s.connect((socket.gethostname(), 1234))
server_address = (socket.gethostname(), 1234)


i = 0
for i in range(0, 1):
    message = "Testing message "
    s.sendto(message.encode('utf-8'), server_address)

'''
for args in sys.argv:
    if args == "":
        args = 'no args'
    else:
        s.send(args + ' ')

print('Goodbye!')



# Test Sender
while True:
    msg = s.recv(1024)
    print(msg.decode("utf-8"))
'''

