package com.example.redthumbapp;

import org.json.simple.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;
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

        double[] feedData;
        double[] feedQualities;
        if(plantData.getFeedData() == null) {
            feedData = null;
            feedQualities = null;
        }else {
            feedData = plantData.getFeedData();
            feedQualities = plantData.getFeedDataQualities();
            //(Sunlight, Humidity,Temperature,SoilMoisture).
            this.sunlight = feedData[0];
            this.temperature = feedData[2];
            this.humidity = feedData[1];
            this.soilMoisture = feedData[3];
        }

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
        if(plantData.getLastWatered() == null){
            return "Never";
        }
        Calendar today = Calendar.getInstance();
        Calendar lastWatered = Calendar.getInstance();
        Timestamp todayTime = new Timestamp(Instant.now().toEpochMilli());
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


    public boolean isData(){
        if(this.plantData.getFeedData() == null ){
            return false;
        }
        if(this.plantData.getFeedDataQualities() == null){
            return false;
        }

        return true;
    }
}
