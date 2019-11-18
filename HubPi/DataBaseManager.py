import sqlite3
import datetime
import time
import random

# As of now, there are three tables.
# One table is used to keep track of a pot plant and it's temperature, humidity, etc, called (plantData)
# Second table is used to keep track of the number of Pots, keep track of it's plant type and pot id, called (pots)
# Third table is used to keep track of the different plant types and it's optimal conditions. Called (plantType)
# The database is called plants.db


class DataBaseManager:
    __connect = None
    __cursor = None

    def __init__(self):
        self.__connect = sqlite3.connect('plants.db')
        self.__cursor = self.__connect.cursor()
        self.__create_tables()

    # initializes all the tables
    def __create_tables(self):
        self.__cursor.execute(
            "CREATE TABLE IF NOT EXISTS pots(pot_id TEXT, plant_type TEXT)")
        self.__cursor.execute(
            "CREATE TABLE IF NOT EXISTS plantData(temperature REAL, humidity REAL, soil_moisture REAL, sunlight INTEGER, timestamp NUMERIC)")
        self.__cursor.execute(
            "CREATE TABLE IF NOT EXISTS plantType(temperature REAL, humidity REAL, soil_moisture REAL, sunlight INTEGER)")

    # Creates a new pot pi to the pots table. Prompted by android app
    def create_pot(self, plant_type, pot_id):
        self.__.cursor.execute(
            "INSERT INTO pots(pot_id, plant_type) VALUES (?, ?)",
            (plant_type, pot_id))

    # Adds the plant data to plant data table
    def add_plant_data(self, temperature, humidity, soil_moisture, sunlight):
        unix = int(time.time())
        date = str(datetime.datetime.fromtimestamp(unix).strftime('%Y-%m-%d %H:%M:%S'))

        self.__.cursor.execute(
            "INSERT INTO plantData(temperature, humidity, soil_moisture, sunlight, date) VALUES (?, ?, ?, ?, ?",
            (temperature, humidity, soil_moisture, sunlight, date))

        self.__connect.commit()

    # creates a new plant type into the database. A string array is passed by the containing the plant information
    # plant_info is an array of strings containing the information about the plant type
    def create_plant_type(self, plant_info):
        temperature = plant_info[0]
        humidity = plant_info[1]
        soil_moisture = plant_info[2]
        sunlight = plant_info[3]

        self.__.cursor.execute(
            "INSERT INTO plantData(temperature, humidity, soil_moisture, sunlight) VALUES (?, ?, ?, ?)",
            (temperature, humidity, soil_moisture, sunlight))

        self.__connect.commit()


if __name__ == "__main__":
    data_base_manager = DataBaseManager()

    for i in range(0, 10):

        # temporary values
        temperature = random.randrange(20, 25)
        humidity = random.randrange(0, 10)
        soil_moisture = random.randrange(0, 10)
        sunlight = True

        # will receive data from the pot pi
        data_base_manager.add_plant_data(temperature, humidity, soil_moisture, sunlight)
        time.sleep(1)
