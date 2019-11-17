package com.example.redthumbapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlantData {

    private static int TEMPERATURE_ERROR = 2;
    private static int MOISTURE_ERROR = 5;
    private static int HUMIDITY_ERROR = 5;

    private JSONObject[] plantDataRaw;
    private JSONObject plantTypeData;

    private int datapoints;

    private Date times[];

    private boolean[] sunLight;
    private int idealSunCoverage;

    private int[] temperature;
    private int idealTemperature;

    private int[] humidity;
    private int idealHumidity;

    private int[] soilMoisture;
    private int idealSoilMoisture;


    /**
     * Default constructor for one data point, used for feed view.
     * @param plantDataRaw: raw data obtained from the database
     */
    public PlantData(JSONObject[] plantDataRaw, JSONObject plantTypeData){
        this.datapoints = plantDataRaw.length;
        this.plantDataRaw = plantDataRaw;
        this.plantTypeData = plantTypeData;
        try {
            for(int i = 0;i < datapoints;i++) {
                this.times[i] = (Date) (plantDataRaw[i].get("time"));
                this.sunLight[i] = plantDataRaw[i].getBoolean("sunlight");
                this.humidity[i] = plantDataRaw[i].getInt("humidity");
                this.temperature[i] = plantDataRaw[i].getInt("temperature");
                this.soilMoisture[i] = plantDataRaw[i].getInt("soil_moisture");
            }
            this.idealSunCoverage = plantTypeData.getInt("sun_coverage");
            this.idealHumidity = plantTypeData.getInt("humidity");
            this.idealTemperature = plantTypeData.getInt("temperature");
            this.idealSoilMoisture = plantTypeData.getInt("soil_moisture");

        }catch(JSONException e1){
            //TODO: Handle this error!
        }
    }

    /**
     * Adds new data to the plantData set, values with matching dates cannot be added.
     * @param plantDataRaw New data to be added to the plantData set.
     */
    public void addPlantData(JSONObject[] plantDataRaw){
        boolean duplicateFlag;
        try{
            for(int i = 0; i < plantDataRaw.length; i ++){
                duplicateFlag = false;
                Date currPlantData = (Date) plantDataRaw[i].get("time");
                for(int j = 0; j < datapoints; j++){
                    if(this.times[j].equals(currPlantData)){
                        duplicateFlag = true;
                        break;
                    }
                }
                if(!duplicateFlag){
                    this.times[datapoints] = (Date) (plantDataRaw[i].get("time"));
                    this.sunLight[datapoints] = plantDataRaw[i].getBoolean("sunlight");
                    this.humidity[datapoints] = plantDataRaw[i].getInt("humidity");
                    this.temperature[datapoints] = plantDataRaw[i].getInt("temperature");
                    this.soilMoisture[datapoints] = plantDataRaw[i].getInt("soil_moisture");
                    datapoints++;
                }
            }

        }catch (JSONException e1){

        }
    }

    //Sensor values record sunlight or no sunlight and give us a sunlight hours measurement,
    //This hours value is assumed to be correct and we won't adjust for error.
    private int getSunCoverageQuality(int sunCoverageHours) {
        return (Math.abs(sunCoverageHours-idealSunCoverage)/((sunCoverageHours+idealSunCoverage)/2))*100;
    }

    //NOTE
    //Quality values are adjusted by the error of the sensor,
    //if the value is within the error of the sensor it gets 100% quality.

    private int getTemperatureQuality(int temperature) {
        int tempDiff = idealTemperature - temperature;
        temperature += (Math.abs(tempDiff) > TEMPERATURE_ERROR ? TEMPERATURE_ERROR * (tempDiff > 0 ? 1 : -1) : tempDiff);

        return (Math.abs(temperature - idealTemperature)/((temperature+idealTemperature)/2))*100;
    }

    private int getSoilMoistureQuality(int soilMoisture) {
        int moistDiff = idealSoilMoisture - soilMoisture;
        soilMoisture += (Math.abs(moistDiff) > MOISTURE_ERROR ? MOISTURE_ERROR * (moistDiff > 0 ? 1 : -1) : moistDiff);

        return ((Math.abs(soilMoisture-idealSoilMoisture))/((soilMoisture+idealSoilMoisture)/2))*100;
    }

    private int getHumidityQuality(int humidity) {
        int humidDiff = idealHumidity - humidity;
        humidity += (Math.abs(humidDiff) > HUMIDITY_ERROR ? HUMIDITY_ERROR * (humidDiff > 0 ? 1 : -1) : humidDiff);

        return (Math.abs((humidity/idealHumidity))/((humidity+idealHumidity)/2))*100;
    }

    /**
     * Creates a subset of the plantData with only values from the startDate to the endDate.
     * @return A list of indexes which belong to the subset
     */
    private List<Integer> createPlantDataSubset(Date startDate, Date endDate){
        if(endDate.before(startDate)){
            throw new IllegalArgumentException("startDate must be before endDate!");
        }
        List<Integer> plantDataSubset = new ArrayList<Integer>();
        for(int i = 0; i < datapoints; i++){
            if(this.times[i].after(startDate) && this.times[i].before(endDate)){
                plantDataSubset.add(i);
            }
        }

        return plantDataSubset;
    }


    /**
     * Gets the index of the values with the most recent date.
     * @return An index of the values with the most recent date.
     */
    private  int getMostRecentDateIndex(){
        //Find the index of the most recent date.
        int maxDate = 0;
        for(int i = 1; i < datapoints; i++){
            if(this.times[maxDate].before(this.times[i])){
                maxDate = i;
            }
        }
        return maxDate;
    }

    /**
     * Gets the most recent date.
     * @return The most recent date.
     */
    public java.sql.Date getMostRecentDate(){
        Date maxDate = this.times[0];
        for(int i = 1; i < datapoints; i++){
            if(maxDate.before(this.times[i])){
                maxDate = this.times[i];
            }
        }

        return maxDate;
    }

    /**
     * Finds the most recent data and packages it as an array of ints. (Length 4).
     * @return An array of ints containing the most recent feed data. (sunLight,Humidity,Temperature,SoilMoisture)
     *
     */
    public int[] getFeedData(){

        int maxDate = getMostRecentDateIndex();

        int[] feedData = new int[4];
        feedData[0] = (this.sunLight[maxDate]) ? 1 : 0; //Convert from boolean to int.
        feedData[1] = this.humidity[maxDate];
        feedData[2] = this.temperature[maxDate];
        feedData[3] = this.soilMoisture[maxDate];

        return feedData;
    }

    /**
     * The feed qualities are the most recent 24 hours of qualities.
     * @return An array of quality ints (Sunlight, Humidity,Temperature,SoilMoisture).
     */
    public int[] getFeedDataQualities(){
        int maxDateIndex = getMostRecentDateIndex();

        //Get date 24 prior to the maxDate.
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getMostRecentDate());
        cal.add(Calendar.DAY_OF_YEAR,-1);
        List<Integer> plantDataSubset = createPlantDataSubset( (Date) cal.getTime(), this.getMostRecentDate());

        int sunCoverage = 0;
        int humidityTotal = 0;
        int temperatureTotal = 0;
        int soilMoistureTotal = 0;

        for(int i : plantDataSubset){
            sunCoverage += (this.sunLight[i]) ? (1/60) : 0; //Add a minute if true, else add 0
            humidityTotal += getHumidityQuality(this.humidity[i]);
            temperatureTotal += getTemperatureQuality(this.temperature[i]);
            soilMoistureTotal += getSoilMoistureQuality(this.soilMoisture[i]);
        }
        int n = plantDataSubset.size();
        int sunCoverageQuality = getSunCoverageQuality(sunCoverage);
        int humidityQuality = humidityTotal/n;
        int temperatureQuality = temperatureTotal/n;
        int soilMoistureQuality = soilMoistureTotal/n;

        int[] feedData = new int[4];
        feedData[0] = sunCoverageQuality;
        feedData[1] = humidityQuality;
        feedData[2] = temperatureQuality;
        feedData[3] = soilMoistureQuality;
        return feedData;
    }


    public Map<Date,Integer[]> getHistoricalFeed(Date startDate, Date endDate){
        List<Integer> plantDataSubset = createPlantDataSubset(startDate,endDate);
        int n = plantDataSubset.size();

        Map<Date,Integer[]> historicalFeedData = new HashMap<>();

        for(int i : plantDataSubset){
            Integer[] currFeedData = new Integer[4];
            currFeedData[0] = this.sunLight[i] ? 1 : 0;
            currFeedData[1] = this.humidity[i];
            currFeedData[2] = this.temperature[i];
            currFeedData[3] = this.soilMoisture[i];
            historicalFeedData.put(this.times[i],currFeedData);
        }

        return historicalFeedData;
    }


}
