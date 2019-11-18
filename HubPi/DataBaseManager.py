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

    def create_table(self):
        self.__cursor.execute(
            "CREATE TABLE IF NOT EXISTS plantData(temperature REAL, humidity REAL, soil_moisture REAL, sunlight INTEGER)")

    def add_plant_data(self, temperature, humidity, soil_moisture, sunlight):
        # unix = int(time.time())
        # date = str(datetime.datetime.fromtimestamp(unix).strftime('%Y-%m-%d %H:%M:%S'))

        self.__.cursor.execute(
            "INSERT INTO plantData(temperature, humidity, soil_moisture, sunlight) VALUES (?, ?, ?, ?)",
            (temperature, humidity, soil_moisture, sunlight))

        self.__connect.commit()


if __name__ == "__main__":
    data_base_manager = DataBaseManager("random string")
    data_base_manager.create_table()

    while True:
        data_base_manager.add_plant_data()
        time.sleep(1)
