import sqlite3
import datetime
import time
import random


class DataBaseManager:
    __potID = ""
    __connect = None
    __cursor = None

    def __init__(self, pot_id):
        self.__potID = pot_id
        self.__connect = sqlite3.connect('plants.db')
        self.__cursor = self.__connect.cursor()

    # creates the table for the plant data if it does not exist
    def create_table(self):
        self.__cursor.execute(
            "CREATE TABLE IF NOT EXISTS plantData(temperature REAL, humidity REAL, soil_moisture REAL, sunlight INTEGER)")

    # adds the plant data to plant data table
    def add_plant_data(self, temperature, humidity, soil_moisture, sunlight):
        # unix = int(time.time())
        # date = str(datetime.datetime.fromtimestamp(unix).strftime('%Y-%m-%d %H:%M:%S'))

        self.__.cursor.execute(
            "INSERT INTO plantData(temperature, humidity, soil_moisture, sunlight) VALUES (?, ?, ?, ?)",
            (temperature, humidity, soil_moisture, sunlight))

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
    data_base_manager = DataBaseManager("random string")
    data_base_manager.create_table()

    # temporary values
    temperature = 25
    humidity = 10
    soil_moisture = 10
    sunlight = True

    while True:

        # will receive data from the pot pi
        data_base_manager.add_plant_data(temperature, humidity, soil_moisture, sunlight)
        time.sleep(1)
