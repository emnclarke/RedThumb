package com.example.redthumbapp;

import java.util.ArrayList;

public class PlantFeedData {
    private int potID;
    private String plantType;

    private double sunlight;
    private double temperature;
    private double humidity;
    private double soilMoisture;

    public PlantFeedData(double[] feedData,int potID,String plantType){
        this.potID = potID;
        this.plantType = plantType;

        this.sunlight = feedData[0];
        this.temperature = feedData[1];
        this.humidity = feedData[2];
        this.soilMoisture = feedData[3];
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

    public static ArrayList<PlantFeedData> createDummyPlants(int n){

        ArrayList<PlantFeedData> plantFeedDataArrayList = new ArrayList<PlantFeedData>();
        for(int i = 0;i<n;i++){
            double[] feedData = new double[4];
            feedData[0] = 1.0;
            feedData[1] = 25.3 + i/5.0;
            feedData[2] = 54.2 + i/2.0;
            feedData[3] = 89.6 - i/1.0;

            plantFeedDataArrayList.add(new PlantFeedData(feedData,i,"Fake Plant Type"));
        }
        return plantFeedDataArrayList;
    }
}
