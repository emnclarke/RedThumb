class PlantData(object):
    def __init__(self, timestamp, potID, temperature, humidity, soilMoisture, sunlight):
        self.timestamp = timestamp
        self.potID = potID
        self.temperature = temperature
        self.humidity = humidity
        self.soilMoisture = soilMoisture
        self.sunlight = sunlight
        
    def toString(self):
        return str(self.timestamp) + " " + str(self.potID) + " " + str(self.temperature) + " " + str(self.humidity) + " " + str(self.soilMoisture) + " " + str(self.sunlight)

class PlantType(object):
    def __init__(self, plantID, name, waterFrequency, waterLength, temperature, humidity, soilMoisture, sunCoverage):
        self.plantID = plantID
        self.name = name
        self.waterFrequency = waterFrequency
        self.waterLength = waterLength
        self.temperature = temperature
        self.humidity = humidity
        self.soilMoisture = soilMoisture
        self.sunCoverage = sunCoverage
        
    def toString(self):
        return str(self.plantID) + " " + str(self.name) + " " + str(self.waterFrequency) + " " + str(self.waterLength) + " " + str(self.temperature) + " " + str(self.humidity) + " " + str(self.soilMoisture) + " " + str(self.sunCoverage)

class SmartPot(object):
    def __init__(self, potID, name, potIP, plantID, lastWatered, lowWater, waterFlag):
        self.potID = potID
        self.name = name
        self.potIP = potIP
        self.plantID = plantID
        self.lastWatered = lastWatered
        self.lowWater = lowWater
        self.waterFlag = waterFlag
        
    def toString(self):
        return str(self.potID) + " " + str(self.name) + " " + str(self.potIP) + " " + str(self.plantID) + " " + str(self.lastWatered) + " " + str(self.lowWater) + " " + str(self.waterFlag)

class TooFastError(RuntimeError):
    def __init__(self, arg):
      self.args = (arg,)

class MissingPlantTypeError(RuntimeError):
    def __init__(self, arg):
      self.args = (arg,)
