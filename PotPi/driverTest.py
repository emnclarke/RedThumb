 #!/usr/bin/env python

import time, json

# Json validator
def isJson(myjson):
  try:
    jsonObject = json.loads(myjson)
  except ValueError:
    return False
  return True

print("Collecting data (takes two seconds)...\n")

dataFile = open("data.txt", 'r')
data = dataFile.read()
dataFile.close()

timestamp1 = data[:15]
dataString = data[16:]

time.sleep(2)

dataFile = open("data.txt", 'r')
data2 = dataFile.read()
dataFile.close()

timestamp2 = data2[:15]
dataString2 = data2[16:]


# Test 2
print ("Test 2, are timestamps updating (arduino is connected)")
if not timestamp1 == timestamp2:
    print ("pass")
else:
    print ("fail")

# Test 3
print ("\nTest 3, are both data points valid")
if not isJson(dataString) and isJson(dataString2):
    print ("pass")
else:
    print ("fail")
