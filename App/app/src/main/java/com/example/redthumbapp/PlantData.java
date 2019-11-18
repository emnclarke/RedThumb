package com.example.redthumbapp;

import org.json.simple.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlantData {

    //Values taken from the hardware documentation.
    private static int TEMPERATURE_ERROR = 2;
    private static int MOISTURE_ERROR = 5;
    private static int HUMIDITY_ERROR = 5;

    //Fields obtained from the database
    private JSONObject[] plantDataRaw;
    private JSONObject plantTypeData;

    //Amount of indivual datapoints in the plantData set.
    private int datapoints;

    //Each of the following are columns in the database.
    //Time
    private ArrayList<Date> times;

    //Sunlight
    private ArrayList<Boolean> sunLight;
    private Double idealSunCoverage;

    //Temperature
    private ArrayList<Double> temperature;
    private Double idealTemperature;

    //Humidity
    private ArrayList<Double> humidity;
    private Double idealHumidity;

    //Soil Moisture
    private ArrayList<Double> soilMoisture;
    private Double idealSoilMoisture;


    /**
     * Default constructor.
     * @param plantDataRaw: raw data obtained from the database
     * @param plantTypeData: plant type data obtained from the database
     */
    public PlantData(JSONObject[] plantDataRaw, JSONObject plantTypeData){
        this.datapoints = plantDataRaw.length;
        this.plantDataRaw = plantDataRaw;
        this.plantTypeData = plantTypeData;

        this.times = new ArrayList<>();
        this.sunLight = new ArrayList<>();
        this.soilMoisture = new ArrayList<>();
        this.humidity = new ArrayList<>();
        this.temperature = new ArrayList<>();

        int newDataPoints = datapoints;
        for(int i = 0;i < newDataPoints;i++) {
            if(plantDataRaw[i].get("time") == null){
                //A date is required!
                datapoints--;
                if(datapoints <= 0){
                    throw new IllegalArgumentException("Atleast one JSONObject must be valid!");
                }
                continue;
            }
            this.times.add((Date) (plantDataRaw[i].get("time")));
            this.sunLight.add((plantDataRaw[i].get("sunlight") == null) ? null : (boolean)plantDataRaw[i].get("sunlight"));
            this.humidity.add((plantDataRaw[i].get("humidity") == null) ? null : (double)plantDataRaw[i].get("humidity"));
            this.temperature.add((plantDataRaw[i].get("temperature")==null) ? null : (double)plantDataRaw[i].get("temperature"));
            this.soilMoisture.add((plantDataRaw[i].get("soil_moisture") == null)? null : (double)plantDataRaw[i].get("soil_moisture"));

        }
        this.idealSunCoverage = (double)plantTypeData.get("sun_coverage");
        this.idealHumidity = (double)plantTypeData.get("humidity");
        this.idealTemperature = (double)plantTypeData.get("temperature");
        this.idealSoilMoisture = (double)plantTypeData.get("soil_moisture");
    }

    /**
     * A simple getter for datapoints.
     * @return The amount of datapoints in the plantData set.
     */
    public int getDatapoints(){
        return datapoints;
    }

    /**
     * Adds new data to the plantData set, values with matching dates cannot be added.
     * @param plantDataRaw New data to be added to the plantData set.
     */
    public void addPlantData(JSONObject[] plantDataRaw){
        boolean duplicateFlag;
        for(int i = 0; i < plantDataRaw.length; i ++){
            duplicateFlag = false;
            Date currPlantData = (Date) plantDataRaw[i].get("time");
            for(int j = 0; j < datapoints; j++){
                if(this.times.get(j).equals(currPlantData)){
                    duplicateFlag = true;
                    break;
                }
            }
            if(!duplicateFlag){
                this.times.add(datapoints,(Date) (plantDataRaw[i].get("time")));
                this.sunLight.add(datapoints,(boolean)plantDataRaw[i].get("sunlight"));
                this.humidity.add(datapoints, (double)plantDataRaw[i].get("humidity"));
                this.temperature.add(datapoints,(double)plantDataRaw[i].get("temperature"));
                this.soilMoisture.add(datapoints,(double)plantDataRaw[i].get("soil_moisture"));
                datapoints++;
            }
        }


        }

    //Sensor values record sunlight or no sunlight and give us a sunlight hours measurement,
    //This hours value is assumed to be correct and we won't adjust for error.
    private Double getSunCoverageQuality(int sunCoverageHours) {
        return (Math.abs(sunCoverageHours-idealSunCoverage)/((sunCoverageHours+idealSunCoverage)/2))*100;
    }

    //NOTE
    //Quality values are adjusted by the error of the sensor,
    //if the value is within the error of the sensor it gets 100% quality.

    private Double getTemperatureQuality(double temperature) {
        Double tempDiff = idealTemperature - temperature;
        temperature += (Math.abs(tempDiff) > TEMPERATURE_ERROR ? TEMPERATURE_ERROR * (tempDiff > 0 ? 1 : -1) : tempDiff);

        return (Math.abs(temperature - idealTemperature)/((temperature+idealTemperature)/2))*100;
    }

    private Double getSoilMoistureQuality(double soilMoisture) {
        Double moistDiff = idealSoilMoisture - soilMoisture;
        soilMoisture += (Math.abs(moistDiff) > MOISTURE_ERROR ? MOISTURE_ERROR * (moistDiff > 0 ? 1 : -1) : moistDiff);

        return ((Math.abs(soilMoisture-idealSoilMoisture))/((soilMoisture+idealSoilMoisture)/2))*100;
    }

    private Double getHumidityQuality(double humidity) {
        Double humidDiff = idealHumidity - humidity;
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
            if(this.times.get(i).after(startDate) && this.times.get(i).before(endDate)){
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
            if(this.times.get(maxDate).before(this.times.get(i))){
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
        Date maxDate = this.times.get(0);
        for(int i = 1; i < datapoints; i++){
            if(maxDate.before(this.times.get(i))){
                maxDate = this.times.get(i);
            }
        }
        return maxDate;
    }

    /**
     * Finds the most recent data and packages it as an array of ints. (Length 4).
     * @return An array of ints containing the most recent feed data. (sunLight,Humidity,Temperature,SoilMoisture)
     *
     */
    public double[] getFeedData(){

        int maxDate = getMostRecentDateIndex();
        double[] feedData = new double[4];
        feedData[0] = (this.sunLight.get(maxDate)) ? 1 : 0; //Convert from boolean to int.
        feedData[1] = this.humidity.get(maxDate);
        feedData[2] = this.temperature.get(maxDate);
        feedData[3] = this.soilMoisture.get(maxDate);

        return feedData;
    }

    /**
     * The feed qualities are the most recent 24 hours of qualities.
     * @return An array of quality ints (Sunlight, Humidity,Temperature,SoilMoisture).
     */
    public double[] getFeedDataQualities(){
        int maxDateIndex = getMostRecentDateIndex();

        //Get date 24 prior to the maxDate.
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getMostRecentDate());
        cal.add(Calendar.DAY_OF_YEAR,-1);
        List<Integer> plantDataSubset = createPlantDataSubset( new Date(cal.getTimeInMillis()), this.getMostRecentDate());

        int sunCoverage = 0;
        int humidityTotal = 0;
        int temperatureTotal = 0;
        int soilMoistureTotal = 0;

        for(int i : plantDataSubset){
            sunCoverage += (this.sunLight.get(i)) ? (1/60) : 0; //Add a minute if true, else add 0
            humidityTotal += getHumidityQuality(this.humidity.get(i));
            temperatureTotal += getTemperatureQuality(this.temperature.get(i));
            soilMoistureTotal += getSoilMoistureQuality(this.soilMoisture.get(i));
        }
        int n = plantDataSubset.size();
        double sunCoverageQuality = getSunCoverageQuality(sunCoverage);
        double humidityQuality = humidityTotal/n;
        double temperatureQuality = temperatureTotal/n;
        double soilMoistureQuality = soilMoistureTotal/n;

        double[] feedData = new double[4];
        feedData[0] = sunCoverageQuality;
        feedData[1] = humidityQuality;
        feedData[2] = temperatureQuality;
        feedData[3] = soilMoistureQuality;
        return feedData;
    }


    /**
     * Returns a subset of plantData determined by the start and end date.
     * @param startDate: The start of the range for the subset.
     * @param endDate: The end of the range for the subset.
     * @return A map of the subset of data, with the datapoint being the value and the time is the key.
     */
    public Map<Date,Double[]> getHistoricalFeed(Date startDate, Date endDate){
        List<Integer> plantDataSubset = createPlantDataSubset(startDate,endDate);
        int n = plantDataSubset.size();

        Map<Date,Double[]> historicalFeedData = new HashMap<>();

        for(int i : plantDataSubset){
            Double[] currFeedData = new Double[4];
            currFeedData[0] = this.sunLight.get(i) ? 1.0 : 0.0;
            currFeedData[1] = this.humidity.get(i);
            currFeedData[2] = this.temperature.get(i);
            currFeedData[3] = this.soilMoisture.get(i);
            historicalFeedData.put(this.times.get(i),currFeedData);
        }

        return historicalFeedData;
    }


}
