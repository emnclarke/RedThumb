package com.example.redthumbapp;

import org.json.simple.JSONObject;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class PlantFeedData {
    public PlantData plantData;
    private String potID;
    private String plantType;

    private double sunlight;
    private double temperature;
    private double humidity;
    private double soilMoisture;

    private double sunlightQuality;
    private double temperatureQuality;
    private double humidityQuality;
    private double soilMoistureQuality;

    public PlantFeedData(PlantData plantData) {

        this.plantData = plantData;

        this.potID = plantData.getPotName();
        this.plantType = plantData.getPlantType();

        double[] feedData = plantData.getFeedData();
        double[] feedQualities = plantData.getFeedDataQualities();
        //(Sunlight, Humidity,Temperature,SoilMoisture).
        this.sunlight = feedData[0];
        this.temperature = feedData[2];
        this.humidity = feedData[1];
        this.soilMoisture = feedData[3];

        if(feedQualities != null) {

            this.sunlightQuality = feedQualities[0];
            this.temperatureQuality = feedQualities[2];
            this.humidityQuality = feedQualities[1];
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

    public String getPotIDInteger() {
        return String.valueOf(plantData.getPotID());
    }
    public String getPotID() {
        return potID;
    }

    public double getHumidity() {
        return humidity;
    }

    public String getLastWatered(){
        Calendar today = Calendar.getInstance();
        Calendar lastWatered = Calendar.getInstance();
        Date todayTime = new Date(Instant.now().toEpochMilli());
        Date lastWateredTime = plantData.getLastWatered();
        today.setTime(todayTime);
        lastWatered.setTime(lastWateredTime);
        if(today.get(Calendar.DAY_OF_YEAR) == lastWatered.get(Calendar.DAY_OF_YEAR) &&
                today.get(Calendar.YEAR) == lastWatered.get(Calendar.YEAR)){
            return "Today";
        }
        today.add(Calendar.DAY_OF_YEAR,-1);
        if(today.get(Calendar.DAY_OF_YEAR) == lastWatered.get(Calendar.DAY_OF_YEAR) &&
                today.get(Calendar.YEAR) == lastWatered.get(Calendar.YEAR)){
            return "Yesterday";
        }
        return plantData.getLastWatered().toString();
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
            PlantData plantData = new PlantData(createGoodPlantData(i), createGoodPlantTypeData(),createGoodPotData());

            plantFeedDataArrayList.add(new PlantFeedData(plantData));
        }
        return plantFeedDataArrayList;
    }

    private  static JSONObject createGoodPotData() {
        JSONObject potDataGood = new JSONObject();
        String[] potNames = {"Kitchen Pot","Bathroom Pot","Hallway Pot","Garage Pot","Livingroom Pot"};
        Random rand = new Random();
        int rand1 = rand.nextInt(5);
        int rand2 = rand.nextInt(120);

        potDataGood.put("name", potNames[rand1]);
        potDataGood.put("pot_id",rand2);
        Date date = new Date(Instant.now().toEpochMilli() - (rand1 * 24*60*60*1000));
        potDataGood.put("last_watered",date);
        return potDataGood;
    }
    private static JSONObject createGoodPlantTypeData() {
        //Create dummy object PlantTypeData
        JSONObject plantTypeDataGood = new JSONObject();
        Random rand = new Random();
        int rand2 = rand.nextInt(5);
        String[] plantTypeNames = {"Sunflower","Moonflower","Tomato","Bean Sprout","Cactus"};
        plantTypeDataGood.put("name", plantTypeNames[rand2]);
        plantTypeDataGood.put("sun_coverage", new Double(8));
        plantTypeDataGood.put("temperature", new Double(23));
        plantTypeDataGood.put("humidity", new Double(50));
        plantTypeDataGood.put("soil_moisture", new Double(90));
        return plantTypeDataGood;

    }

    private static JSONObject[] createGoodPlantData(int i) {
        Random rand = new Random();
        int dataPoints = rand.nextInt(500) + 8;
        JSONObject[] JSONarr = new JSONObject[dataPoints];
        for (int j = 0; j < dataPoints; j++) {
            i += j;
            Date date = new Date(Instant.now().toEpochMilli() - (i * 10));
            JSONObject plantDataRawGood = new JSONObject();
            plantDataRawGood.put("time", date);
            plantDataRawGood.put("pot_id", new Integer(i+j));
            plantDataRawGood.put("sunlight", new Boolean(true));
            plantDataRawGood.put("temperature", new Double(23.1));
            plantDataRawGood.put("humidity", new Double(40.6 ));
            plantDataRawGood.put("soil_moisture", new Double(69.2 ));
            JSONarr[j] = plantDataRawGood;
        }
        return JSONarr;
    }
}
