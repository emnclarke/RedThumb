package com.example.redthumbapp;

import org.json.simple.JSONObject;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

public class PlantFeedData {
    private int potID;
    private String plantType;

    private double sunlight;
    private double temperature;
    private double humidity;
    private double soilMoisture;

    private double sunlightQuality;
    private double temperatureQuality;
    private double humidityQuality;
    private double soilMoistureQuality;

    public PlantFeedData(double[] feedData, double[] feedQualities, int potID, String plantType) {
        this.potID = potID;
        this.plantType = plantType;

        this.sunlight = feedData[0];
        this.temperature = feedData[1];
        this.humidity = feedData[2];
        this.soilMoisture = feedData[3];

        if(feedQualities != null) {
            this.sunlightQuality = feedQualities[0];
            this.temperatureQuality = feedQualities[1];
            this.humidityQuality = feedQualities[2];
            this.soilMoistureQuality = feedQualities[3];
        }else{
            this.sunlightQuality = 0.0;
            this.temperatureQuality = 0.0;
            this.humidityQuality = 0.0;
            this.soilMoistureQuality = 0.0;
        }

    }

    public String getPlantType() {
        return plantType;
    }

    public int getPotID() {
        return potID;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getSoilMoisture() {
        return soilMoisture;
    }

    public double getSunlight() {
        return sunlight;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidityQuality() {
        return humidityQuality;
    }

    public double getSoilMoistureQuality() {
        return soilMoistureQuality;
    }

    public double getSunlightQuality() {
        return sunlightQuality;
    }

    public double getTemperatureQuality() {
        return temperatureQuality;
    }

    public static ArrayList<PlantFeedData> createDummyPlants(int n) {

        ArrayList<PlantFeedData> plantFeedDataArrayList = new ArrayList<PlantFeedData>();
        for (int i = 0; i < n; i++) {
            PlantData plantData = new PlantData(createGoodPlantData(i), createGoodPlantTypeData());

            plantFeedDataArrayList.add(new PlantFeedData(plantData.getFeedData(), plantData.getFeedDataQualities(), i, "Fake Plant Type"));
        }
        return plantFeedDataArrayList;
    }

    private static JSONObject createGoodPlantTypeData() {
        //Create dummy object PlantTypeData
        JSONObject plantTypeDataGood = new JSONObject();
        plantTypeDataGood.put("plant_type", 1);
        plantTypeDataGood.put("name", "Testhead Palm");
        plantTypeDataGood.put("sun_coverage", new Double(8));
        plantTypeDataGood.put("temperature", new Double(23));
        plantTypeDataGood.put("humidity", new Double(50));
        plantTypeDataGood.put("soil_moisture", new Double(90));
        return plantTypeDataGood;

    }

    private static JSONObject[] createGoodPlantData(int i) {
        Random rand = new Random();
        int dataPoints = rand.nextInt(50) + 8;
        JSONObject[] JSONarr = new JSONObject[dataPoints];
        for (int j = 0; j < dataPoints; j++) {
            i += j;
            Date date = new Date(Instant.now().toEpochMilli() - (i * 10));
            JSONObject plantDataRawGood = new JSONObject();
            plantDataRawGood.put("time", date);
            plantDataRawGood.put("pot_id", new Integer(i+j));
            plantDataRawGood.put("sunlight", new Boolean(i % 2 == 1));
            plantDataRawGood.put("temperature", new Double(23.1 + (i+j+1)));
            plantDataRawGood.put("humidity", new Double(50.6 + (i+j+4)));
            plantDataRawGood.put("soil_moisture", new Double(89.2 + (i+j)));
            JSONarr[j] = plantDataRawGood;
        }
        return JSONarr;
    }
}
