#!/usr/bin/env python

import datetime
import mysql.connector
from redThumbDataClasses import *

class DBManager(object):
    redThumbdb = None
    dbcursor = None

    def __init__(self):
        self.redThumbdb = mysql.connector.connect(
            host="localhost",
            user="python",
            passwd="python",
            database="red_thumb"
        )
        self.cursor = self.redThumbdb.cursor()
        self.__createTables()

    # initializes all the tables if they don't already exist
    def __createTables(self):
        self.cursor.execute("CREATE TABLE IF NOT EXISTS SmartPots (pot_id int(11) NOT NULL AUTO_INCREMENT, name tinytext COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'no_name', pot_ip tinytext COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0.0.0.0', plant_type int(11) NOT NULL DEFAULT 1, last_watered date DEFAULT NULL, low_water tinyint(1) NOT NULL DEFAULT 0, water_flag tinyint(1) NOT NULL DEFAULT 0, PRIMARY KEY (pot_id)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci")
        self.cursor.execute("CREATE TABLE IF NOT EXISTS PlantTypes (plant_type int(11) NOT NULL AUTO_INCREMENT,name tinytext COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'no_name',water_frequency int(11) NOT NULL DEFAULT 1,water_length int(11) NOT NULL DEFAULT 1,temperature float NOT NULL DEFAULT 20,humidity float NOT NULL DEFAULT 40,soil_moisture tinytext COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'wet',sun_coverage int(11) NOT NULL DEFAULT 6,PRIMARY KEY (plant_type)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci")
        self.cursor.execute("CREATE TABLE IF NOT EXISTS PlantData (time timestamp NOT NULL DEFAULT current_timestamp(), pot_id int(11) NOT NULL, temperature float DEFAULT NULL, humidity float DEFAULT NULL, soil_moisture tinytext COLLATE utf8mb4_unicode_ci DEFAULT NULL, sunlight tinyint(1) DEFAULT NULL, PRIMARY KEY (time)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci")
    
    
    # Plant type methods
    
    def _getValidPlantVariables(self, plant):
        variables = []
        
        if plant.name is not None:
            if not isinstance(plant.name, str):
                raise Exception("Plant name must be a string. Got: " + str(type(plant.name)))
            if not len(plant.name) <= 63:
                raise Exception("Plant name must be less than 64 characters. Length was " + str(len(plant.name)))
            name = "'" + plant.name + "'"
            variables.append(("name", name))
            
        if plant.waterFrequency is not None:
            if not isinstance(plant.waterFrequency, int):
                raise Exception("Water frequency must be an int. Got: " + str(type(plant.waterFrequency)))
            if plant.waterFrequency == 0:
                raise Exception("Water frequency cannot be 0")
            variables.append(("water_frequency", plant.waterFrequency))
        
        if plant.waterLength is not None:
            if not isinstance(plant.waterLength, int):
                raise Exception("Water length must be an int. Got: " + str(type(plant.waterLength)))
            if plant.waterFrequency == 0:
                raise Exception("Water length cannot be 0")
            variables.append(("water_length", plant.waterLength))
        
        if plant.temperature is not None:
            if not (isinstance(plant.temperature, float) or isinstance(plant.temperature, int)):
                raise Exception("Temperature must be a float or int. Got: " + str(type(plant.temperature)))
            variables.append(("temperature", plant.temperature))
        
        if plant.humidity is not None:
            if not (isinstance(plant.humidity, float) or isinstance(plant.humidity, int)):
                raise Exception("Humidity must be a float or int. Got: " + str(type(plant.humidity)))
            variables.append(("humidity", plant.humidity))
            
        if plant.soilMoisture is not None:
            acceptableValues = ["dry", "wet", "water"]
            if not plant.soilMoisture in acceptableValues:
                raise Exception("Submitted soil moisture value not valid. Expecting {\"dry\", \"wet\", \"water\"} got: " + str(plant.soilMoisture))
            soilMoisture = "'" + plant.soilMoisture + "'"
            variables.append(("soil_moisture", soilMoisture))
        
        if plant.sunCoverage is not None:
            if not isinstance(plant.sunCoverage, int):
                raise Exception("Sun coverage must be an int. Got: " + str(type(plant.sunCoverage)))
            variables.append(("sun_coverage", plant.sunCoverage))
            
        return variables
    
    def submitPlantType(self, plant):
        if not isinstance(plant, PlantType):
            raise Exception("Plant must be of type PlantType. Got: " + str(type(plant)))
    
        variables = self._getValidPlantVariables(plant)
        nameString = ""
        variableString = ""
                
        for variable in variables:
            nameString += variable[0] + ", "
            variableString += str(variable[1]) + ", "
            
        if nameString != "":
            nameString = nameString[:-2]
            variableString = variableString[:-2]
            
        sql = "INSERT INTO PlantTypes (%s) VALUES (%s)" % (nameString, variableString)
        self.cursor.execute(sql)
        self.redThumbdb.commit()
        
    def updatePlantType(self, plant):
        if not isinstance(plant, PlantType):
            raise Exception("Plant must be of type PlantType. Got: " + str(type(plant)))
        if not isinstance(plant.plantID, int):
            raise Exception("plantID must be int. Got: " + str(type(plant.plantID)))
    
        variables = self._getValidPlantVariables(plant)
        updateString = ""
        
        for variable in variables:
            updateString += variable[0] + "=" + str(variable[1]) + ", "
            
        if updateString != "":
            updateString = updateString[:-2]
        
        sql = "UPDATE PlantTypes SET %s WHERE plant_id=%s" % (updateString, str(plant.plantID))
        self.cursor.execute(sql)
        self.redThumbdb.commit()
        
    def _convertDBtoPlantType(self, plantDB):
        plant = PlantType(plantDB[0],plantDB[1],plantDB[2],plantDB[3],plantDB[4],plantDB[5],plantDB[6],plantDB[7])
        return plant
    
    def fetchPlantType(self, plantID):
        if not isinstance(plantID, int):
            raise Exception("plantID must be int. Got: " + str(type(plant.plantID)))
        
        sql = "SELECT * FROM PlantTypes WHERE plant_id=%s" % (str(plantID))
        self.cursor.execute(sql)
        plantDB = self.cursor.fetchone()
        
        return self._convertDBtoPlantType(plantDB)
        
    def fetchPlantTypes(self):
        sql = "SELECT * FROM PlantTypes"
        self.cursor.execute(sql)
        plantDBList = self.cursor.fetchall()
        
        plants = []
        
        for plantDB in plantDBList:
            plants.append(self._convertDBtoPlantType(plantDB))
        
        return plants
        
    def deletePlantType(self, plantID):
        if not isinstance(plantID, int):
            raise Exception("plantID must be int. Got: " + str(type(plant.plantID)))
        
        sql = "DELETE FROM PlantTypes WHERE plant_id=%s" % (str(plantID))
        self.cursor.execute(sql)
        self.redThumbdb.commit()
    
    
    
    # Pot methods
    
    def _getValidPotVariables(self, pot):
        variables = []
        
        if pot.name is not None:
            if not isinstance(pot.name, str):
                raise Exception("Pot name must be a string. Got: " + str(type(pot.name)))
            if not len(pot.name) <= 63:
                raise Exception("Pot name must be less than 64 characters. Length was " + str(len(pot.name)))
            name = "'" + pot.name + "'"
            variables.append(("name", name))
            
        if pot.potIP is not None:
            if not isinstance(pot.potIP, str):
                raise Exception("Pot ip must be a string. Got: " + str(type(pot.name)))
            potIP = "'" + pot.potIP + "'"
            variables.append(("pot_ip", potIP))
        
        if pot.plantID is not None:
            if not isinstance(pot.plantID, int):
                raise Exception("Plant ID must be an int. Got: " + str(type(pot.plantID)))
            variables.append(("plant_id", pot.plantID))
        
        if pot.lastWatered is not None:
            variables.append(("last_watered", "CURRENT_DATE"))
        
        if pot.lowWater is not None:
            if not (isinstance(pot.lowWater, int) or isinstance(pot.lowWater, bool)):
                raise Exception("Low water must be an int or bool. Got: " + str(type(pot.lowWater)))
            lowWater = str(int(bool(pot.lowWater)))
            variables.append(("low_water", lowWater))
        
        if pot.waterFlag is not None:
            if not (isinstance(pot.waterFlag, int) or isinstance(pot.waterFlag, bool)):
                raise Exception("Water flag must be an int or bool. Got: " + str(type(pot.waterFlag)))
            waterFlag = str(int(bool(pot.waterFlag)))
            variables.append(("water_flag", waterFlag))
            
        return variables
    
    def submitPot(self, pot):
        if not isinstance(pot, SmartPot):
            raise Exception("Pot must be of type SmartPot. Got: " + str(type(pot)))
            
        variables = self._getValidPotVariables(pot)
        nameString = ""
        variableString = ""
                
        for variable in variables:
            nameString += variable[0] + ", "
            variableString += str(variable[1]) + ", "
            
        if nameString != "":
            nameString = nameString[:-2]
            variableString = variableString[:-2]
            
        sql = "INSERT INTO SmartPots (%s) VALUES (%s)" % (nameString, variableString)
        self.cursor.execute(sql)
        self.redThumbdb.commit()
        
    def updatePot(self, pot):
        if not isinstance(pot, SmartPot):
            raise Exception("Pot must be of type SmartPot. Got: " + str(type(pot)))
        if not isinstance(pot.potID, int):
            raise Exception("potID must be int. Got: " + str(type(pot.potID)))
    
        variables = self._getValidPotVariables(pot)
        updateString = ""
        
        for variable in variables:
            updateString += variable[0] + "=" + str(variable[1]) + ", "
            
        if updateString != "":
            updateString = updateString[:-2]
        
        sql = "UPDATE SmartPots SET %s WHERE pot_id=%s" % (updateString, str(pot.potID))
        self.cursor.execute(sql)
        self.redThumbdb.commit()
    
    def _convertDBtoSmartPot(self, potDB):
        pot = SmartPot(potDB[0],potDB[1],potDB[2],potDB[3],potDB[4],potDB[5],potDB[6])
        return pot
    
    def fetchPot(self, potID):
        if not isinstance(potID, int):
            raise Exception("potID must be int. Got: " + str(type(pot.potID)))
        
        sql = "SELECT * FROM SmartPots WHERE pot_id=%s" % (str(potID))
        self.cursor.execute(sql)
        potDB = self.cursor.fetchone()
        
        return self._convertDBtoSmartPot(potDB)
        
    def fetchPots(self):
        sql = "SELECT * FROM SmartPots"
        self.cursor.execute(sql)
        potDBList = self.cursor.fetchall()
        
        pots = []
        
        for potDB in potDBList:
            pots.append(self._convertDBtoSmartPot(potDB))
        
        return pots
        
    def deletePot(self, potID):
        if not isinstance(potID, int):
            raise Exception("potID must be int. Got: " + str(type(pot.potID)))
        
        sql = "DELETE FROM SmartPots WHERE pot_id=%s" % (str(potID))
        self.cursor.execute(sql)
        self.redThumbdb.commit()
    
    
    
    
    # Plant data methods
        
    def submitPlantData(self, data):
        if not isinstance(data, PlantData):
            raise Exception("Data must be of type PlantData. Got: " + str(type(plant)))
    
        variables = []
        
        if data.potID is not None:
            if not isinstance(data.potID, int):
                raise Exception("Pot ID must be an int. Got: " + str(type(data.potID)))
            variables.append(("pot_id", data.potID))
            
        if data.temperature is not None:
            if not (isinstance(data.temperature, float) or isinstance(data.temperature, int)):
                raise Exception("Temperature must be a float or int. Got: " + str(type(data.temperature)))
            variables.append(("temperature", data.temperature))
        
        if data.humidity is not None:
            if not (isinstance(data.humidity, float) or isinstance(data.humidity, int)):
                raise Exception("Humidity must be a float or int. Got: " + str(type(data.humidity)))
            variables.append(("humidity", data.humidity))
            
        if data.soilMoisture is not None:
            acceptableValues = ["dry", "wet", "water"]
            if not data.soilMoisture in acceptableValues:
                raise Exception("Submitted soil moisture value not valid. Expecting {\"dry\", \"wet\", \"water\"} got: " + str(data.soilMoisture))
            soilMoisture = "'" + data.soilMoisture + "'"
            variables.append(("soil_moisture", soilMoisture))
        
        if data.sunlight is not None:
            if not (isinstance(data.sunlight, int) or isinstance(data.sunlight, bool)):
                raise Exception("Sunlight must be an int or bool. Got: " + str(type(data.sunlight)))
            sunlight = str(int(bool(data.sunlight)))
            variables.append(("sunlight", sunlight))
            
        nameString = ""
        variableString = ""
                
        for variable in variables:
            nameString += variable[0] + ", "
            variableString += str(variable[1]) + ", "
            
        if nameString != "":
            nameString = nameString[:-2]
            variableString = variableString[:-2]
            
        sql = "INSERT INTO PlantData (%s) VALUES (%s)" % (nameString, variableString)
        self.cursor.execute(sql)
        self.redThumbdb.commit()
        
    def _convertDBtoPlantData(self, plantDataDB):
        plantData = PlantData(plantDataDB[0],plantDataDB[1],plantDataDB[2],plantDataDB[3],plantDataDB[4],plantDataDB[5])
        return plantData
    
    def fetchCurrentData(self, potID):
        if not isinstance(potID, int):
            raise Exception("potID must be int. Got: " + str(type(potID)))
            
        sql = "SELECT * FROM PlantData WHERE pot_id=%s ORDER BY timestamp DESC LIMIT 1" % (str(potID))
        self.cursor.execute(sql)
        plantDataDB = self.cursor.fetchone()
        
        plantData = self._convertDBtoPlantData(plantDataDB)
        
        return plantData
    
    def fetchAllData(self, potID):
        if not isinstance(potID, int):
            raise Exception("potID must be int. Got: " + str(type(potID)))
            
        sql = "SELECT * FROM PlantData WHERE pot_id=%s" % (str(potID))
        self.cursor.execute(sql)
        plantDataDBList = self.cursor.fetchall()
        
        plantData = []
        
        for plantDataDB in plantDataDBList:
            plantData.append(self._convertDBtoPlantData(plantDataDB))
        
        return plantData
    
    def fetchRecentData(self, potID, numDataPoints):
        if not isinstance(potID, int):
            raise Exception("potID must be int. Got: " + str(type(potID)))
        if not isinstance(numDataPoints, int):
            raise Exception("numDataPoints must be int. Got: " + str(type(timestamp)))
            
        sql = "SELECT * FROM PlantData WHERE pot_id=%s ORDER BY timestamp DESC LIMIT %s" % (str(potID), str(numDataPoints))
        self.cursor.execute(sql)
        plantDataDBList = self.cursor.fetchall()
        
        plantData = []
        
        for plantDataDB in plantDataDBList:
            plantData.append(self._convertDBtoPlantData(plantDataDB))
        
        return plantData
    
    def deleteData(self, timestamp):
        if not isinstance(timestamp, datetime):
            raise Exception("Timestamp must be datetime. Got: " + str(type(timestamp)))
        
        sql = "DELETE FROM SmartPots WHERE timestamp=%s" % (str(timestamp))
        self.cursor.execute(sql)
        self.redThumbdb.commit()
        




if __name__ == "__main__":
    # plant = PlantType(6, ":)", 1, 1, 12, 12, "dry", 1)
    # testPot = SmartPot(4, "newpot2", "1.1.1.1", 3, 1, 1, 1)
    # testPot2 = SmartPot(2, None, None, None, None, 1, 1)

    dbManager = DBManager()
    
    print("no error")
