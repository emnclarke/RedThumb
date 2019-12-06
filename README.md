# 🌵 RedThumb - Smart Pot 🌵
The Red Thumb Smart Pot is an autonomous plant sustaining system that is capable of monitoring the plants through remote access. This is to encourage the presence of house plants in your home by creating an easy to use system to attend to the plants, and overall, promoting a healthier and more natural environment for them to not only survive, but thrive.

This Project was completed as part of the SYSC 3010 - Computer Systems Development Project at Carleton University. Enjoy!

## Getting Started
#### PREREQUISITES
Before you begin you'll need a few things
###### HARDWARE COMPONENTS
		- At least 2 Raspberry Pis (1 hub, as many pots as wanted)
		- At 1 Arduino (1 per pot)
		- Sensors (per pot): 
			- DHT11 (temperature / humidity)
			- Photoresistor
			- Soil Moisture Sensor v1.2
			- Water sensor
		- Pump (per pot)
		- Network Switch / Wireless Router

###### SOFTWARE COMPONENTS
		- The files found in this GIT of course
			- /HubPi contains all hub files
			- /PotPi contains all pot files
			- /App contains android app files
			- /Arduino contains the arduino script
			- /mysql contains the database
		- MySQL
		- Node.Js

#### INSTALLING


## Testing
The programs used to test the functionality of the system are

	- ArduinoStub.py
	- StubDataTest.py
	- systemMock.py
	- driverTest.py
	- The files in /HubPi/Stub Code


## Deployment

1. Setup the Raspbery Pis with static IPs
2. Copy HubPi files to the hub and PotPi files to the pots
3. Edit files and replace IPs with your IPs
4. Install Node.Js and MySQL on the Hub
5. Create Database in line with the example database
6. Make scripts executable and set arduinoCom.py, pot.py, PotManager.py, and app.js to run on startup
7. Load Arduino code onto Arduinos
8. Wire hardware according to schematic
9. Load Android app onto phone
10. Grow plants!

## Our Team
Project was completed by Group W8 during the Fall 2019 term by the following students:
- Emily Clarke
- Stephen Wicklund
- Alden Ng
- Aedyn Ladd
