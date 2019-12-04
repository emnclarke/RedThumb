package com.example.redthumbapp;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;
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
    private ArrayList<JSONObject> plantDataRaw;
    private JSONObject plantTypeData;

    private String potName;
    private String plantType;
    private Date lastWatered;
    private boolean lowWater;
    private int potID;
    //Amount of individual datapoints in the plantData set.
    private int datapoints;

    //Each of the following are columns in the database.
    //Time
    private ArrayList<Timestamp> times;

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
     *
     * @param plantDataRawArr:  raw data obtained from the database
     * @param plantTypeData: plant type data obtained from the database
     */
    public PlantData(JSONArray plantDataRawArr, JSONObject plantTypeData, JSONObject potData) {
        this.datapoints = plantDataRawArr.size();
        plantDataRaw = new ArrayList<JSONObject>();
        for(Object obj : plantDataRawArr){
            this.plantDataRaw.add((JSONObject) obj);
        }
        this.plantTypeData = plantTypeData;

        this.times = new ArrayList<>();
        this.sunLight = new ArrayList<>();
        this.soilMoisture = new ArrayList<>();
        this.humidity = new ArrayList<>();
        this.temperature = new ArrayList<>();

        int newDataPoints = datapoints;
        for (int i = 0; i < newDataPoints; i++) {
            if (plantDataRaw.get(i).get("timestamp") == null) {
                //A date is required! (Without this, the datapoint is useless)
                datapoints--;
                if (datapoints <= 0) {
                    throw new IllegalArgumentException("Atleast one JSONObject must be valid!");
                }
                continue;
            }
            this.times.add(((Timestamp.valueOf((String) plantDataRaw.get(i).get("timestamp")))));
            Double sunlightTemp = Double.valueOf((long) plantDataRaw.get(i).get("sunlight"));
            this.sunLight.add((plantDataRaw.get(i).get("sunlight") == null) ? null : (sunlightTemp.equals(1.0)));
            System.out.println();
            this.humidity.add((plantDataRaw.get(i).get("humidity") == null) ? null : Double.valueOf((Double) plantDataRaw.get(i).get("humidity")));
            this.temperature.add((plantDataRaw.get(i).get("temperature") == null) ? null : Double.valueOf((Double) plantDataRaw.get(i).get("temperature")));
            this.soilMoisture.add(convertSoilMoisture((String) plantDataRaw.get(i).get("soil_moisture")));
//            this.soilMoisture.add((plantDataRaw.get(i).get("soil_moisture") == null) ? null : Double.valueOf((Double) plantDataRaw.get(i).get("soil_moisture")));

        }
        this.idealSunCoverage = Double.valueOf((Long) plantTypeData.get("sun_coverage"));
        this.idealHumidity = Double.valueOf((double) plantTypeData.get("humidity"));
        this.idealTemperature = Double.valueOf((double) plantTypeData.get("temperature"));
        this.idealSoilMoisture = convertSoilMoisture(( String)plantTypeData.get("soil_moisture"));
        this.plantType = (String) (plantTypeData.get("name"));


        //Pot data
        this.potName = (String) (potData.get("name"));
        if(((potData.get("last_watered") == null)? "None" : potData.get("last_watered")).equals("None")){
            this.lastWatered = null;
        }else {
            this.lastWatered = Date.valueOf((String) potData.get("last_watered"));
        }
        this.potID = Math.toIntExact((Long) potData.get("pot_id"));
        this.lowWater = Math.toIntExact((Long) potData.get("low_water")) == 1;;
    }

    private Double convertSoilMoisture(String soilMoisture) {
        if(soilMoisture.equals("dry")){
            return 0.0;
        }
        if(soilMoisture.equals("wet")){
            return 50.0;
        }
        if(soilMoisture.equals("water")){
            return 100.0;
        }
        return null;
    }

    /**
     * A simple getter for datapoints.
     *
     * @return The amount of datapoints in the plantData set.
     */
    public int getDatapoints() {
        return datapoints;
    }

    /**
     * A simple getting for the plantType.
     * @return Plant Type as a string
     */
    public String getPlantType(){ return this.plantType; }

    /**
     * A simple getter for the plantName.
     * @return Plant name as a string
     */
    public String getPotName(){ return this.potName; }

    public Date getLastWatered(){
        return lastWatered;
    }
    public int getPotID(){
        return this.potID;
    }
    /**
     * Adds new data to the plantData set, values with matching dates cannot be added.
     *
     */
//    public void add(ArrayList<JSONObject> plantDataRaw) {
//        boolean duplicateFlag;
//        for (int i = 0; i < plantDataRaw.length; i++) {
//            duplicateFlag = false;
//            Date currPlantData = (Date) plantDataRaw[i].get("time");
//            for (int j = 0; j < datapoints; j++) {
//                if (this.times.get(j).equals(currPlantData)) {
//                    duplicateFlag = true;
//                    break;
//                }
//            }
//            if (!duplicateFlag) {
//                this.times.add(datapoints, (Date) (plantDataRaw[i].get("time")));
//                this.sunLight.add(datapoints, (boolean) plantDataRaw[i].get("sunlight"));
//                this.humidity.add(datapoints, (double) plantDataRaw[i].get("humidity"));
//                this.temperature.add(datapoints, (double) plantDataRaw[i].get("temperature"));
//                this.soilMoisture.add(datapoints, (double) plantDataRaw[i].get("soil_moisture"));
//                datapoints++;
//            }
//
//
//
//    }

    //Sensor values record sunlight or no sunlight and give us a sunlight hours measurement,
    //This hours value is assumed to be correct and we won't adjust for error.
    private Double getSunCoverageQuality(double sunCoverageHours) {
        if(sunCoverageHours>idealSunCoverage){
            return 100.0;
        }
        return 100-(Math.abs(sunCoverageHours - idealSunCoverage) / (idealSunCoverage)) * 100;
    }

    //NOTE
    //Quality values are adjusted by the error of the sensor,
    //if the value is within the error of the sensor it gets 100% quality.

    //This is done by flooring the value to the idealValue within the sensor error

    private Double getTemperatureQuality(double temperature) {
        Double tempDiff = idealTemperature - temperature;
        temperature += (Math.abs(tempDiff) > TEMPERATURE_ERROR ? TEMPERATURE_ERROR * (tempDiff > 0 ? 1 : -1) : tempDiff);

        return 100 - ((Math.abs(temperature - idealTemperature) / (idealTemperature) * 100));
    }

    private Double getSoilMoistureQuality(double soilMoisture) {
        Double moistDiff = idealSoilMoisture - soilMoisture;
        soilMoisture += (Math.abs(moistDiff) > MOISTURE_ERROR ? MOISTURE_ERROR * (moistDiff > 0 ? 1 : -1) : moistDiff);

        return 100 - (((Math.abs(soilMoisture - idealSoilMoisture)) / ((idealSoilMoisture)) * 100));
    }

    private Double getHumidityQuality(double humidity) {
        Double humidDiff = idealHumidity - humidity;
        humidity += (Math.abs(humidDiff) > HUMIDITY_ERROR ? HUMIDITY_ERROR * (humidDiff > 0 ? 1 : -1) : humidDiff);

        return 100 - ((Math.abs((humidity - idealHumidity)) / (idealHumidity)) * 100);
    }

    /**
     * Creates a subset of the plantData with only values from the startDate to the endDate.
     *
     * @return A list of indexes which belong to the subset
     */
    private List<Integer> createPlantDataSubset(Timestamp startDate, Timestamp endDate) {
        if (endDate.before(startDate)) {
            throw new IllegalArgumentException("startDate must be before endDate!");
        }
        List<Integer> plantDataSubset = new ArrayList<Integer>();
        for (int i = 0; i < datapoints; i++) {
            if (this.times.get(i).after(startDate) && this.times.get(i).before(endDate)) {
                plantDataSubset.add(i);
            }
        }

        return plantDataSubset;
    }


    /**
     * Gets the index of the values with the most recent date.
     *
     * @return An index of the values with the most recent date.
     */
    private int getMostRecentDateIndex() {
        //Find the index of the most recent date.
        int maxDate = 0;
        for (int i = 1; i < datapoints; i++) {
            if (this.times.get(maxDate).before(this.times.get(i))) {
                maxDate = i;
            }
        }
        return maxDate;
    }

    /**
     * Gets the most recent date.
     *
     * @return The most recent date.
     */
    public Timestamp getMostRecentDate() {
        Timestamp maxDate = this.times.get(0);
        for (int i = 1; i < datapoints; i++) {
            if (maxDate.before(this.times.get(i))) {
                maxDate = this.times.get(i);
            }
        }
        return maxDate;
    }

    public JSONObject getPlantTypeData(){
        return plantTypeData;
    }

    /**
     * Finds the most recent data and packages it as an array of ints. (Length 4).
     *
     * @return An array of ints containing the most recent feed data. (sunLight,Humidity,Temperature,SoilMoisture)
     */
    public double[] getFeedData() {
        if(this.datapoints == 0){
            return null;
        }
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
     *
     * @return An array of quality ints (Sunlight, Humidity,Temperature,SoilMoisture).
     */
    public double[] getFeedDataQualities() {
        if(this.datapoints == 0){
            return null;
        }
        int maxDateIndex = getMostRecentDateIndex();

        //Get date 24 prior to the maxDate.
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getMostRecentDate());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        List<Integer> plantDataSubset = createPlantDataSubset(new Timestamp(cal.getTimeInMillis()), this.getMostRecentDate());

        double sunCoverage = 0;
        double humidityTotal = 0;
        double temperatureTotal = 0;
        double soilMoistureTotal = 0;

        for (int i : plantDataSubset) {
            sunCoverage += (this.sunLight.get(i)) ? (1.0 / 60.0) : 0; //Add a minute if true, else add 0
            humidityTotal += getHumidityQuality(this.humidity.get(i));
            temperatureTotal += getTemperatureQuality(this.temperature.get(i));
            soilMoistureTotal += getSoilMoistureQuality(this.soilMoisture.get(i));
        }
        int n = plantDataSubset.size();
        double[] feedData;
        if (n > 0) {
//            System.out.println("Sun Coverage Hours: " + sunCoverage);
            double sunCoverageQuality = getSunCoverageQuality(sunCoverage);
            double humidityQuality = humidityTotal / n;
            double temperatureQuality = temperatureTotal / n;
            double soilMoistureQuality = soilMoistureTotal / n;

            feedData = new double[5];
            feedData[0] = (double)Math.round(sunCoverageQuality * 100d) / 100d;
            feedData[1] = (double)Math.round(humidityQuality * 100d) / 100d;
            feedData[2] = (double)Math.round(temperatureQuality * 100d) / 100d;
            feedData[3] = (double)Math.round(soilMoistureQuality * 100d) / 100d;
            feedData[4] = (double)Math.round(sunCoverage * 100d) / 100d;
        } else {
            feedData = null;
        }
        return feedData;
    }


    /**
     * Returns a subset of plantData determined by the start and end date.
     *
     * @param startDate: The start of the range for the subset.
     * @param endDate:   The end of the range for the subset.
     * @return A map of the subset of data, with the datapoint being the value and the time is the key.
     */
    public Map<Timestamp, Double[]> getHistoricalFeed(Timestamp startDate, Timestamp endDate) {
        List<Integer> plantDataSubset = createPlantDataSubset(startDate, endDate);
        int n = plantDataSubset.size();

        Map<Timestamp, Double[]> historicalFeedData = new HashMap<>();

        for (int i : plantDataSubset) {
            Double[] currFeedData = new Double[4];
            currFeedData[0] = this.sunLight.get(i) ? 1.0 : 0.0;
            currFeedData[1] = this.humidity.get(i);
            currFeedData[2] = this.temperature.get(i);
            currFeedData[3] = this.soilMoisture.get(i);
            historicalFeedData.put(this.times.get(i), currFeedData);
        }

        return historicalFeedData;
    }

    /**
     *
     * @return Double length 3 (Temperature Average, Humidity Average, Soil Moisture Average)
     */
    public double[] getDailyAverages(){
        int maxDateIndex = getMostRecentDateIndex();
        double[] average = new double[3];
        //Get date 24 prior to the maxDate.
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getMostRecentDate());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        List<Integer> plantDataSubset = createPlantDataSubset(new Timestamp(cal.getTimeInMillis()), this.getMostRecentDate());
        double humidityTotal = 0;
        double temperatureTotal = 0;
        double soilMoistureTotal = 0;
        for (int i : plantDataSubset) {
            humidityTotal += this.humidity.get(i);
            temperatureTotal += this.temperature.get(i);
            soilMoistureTotal += this.soilMoisture.get(i);
        }

        average[0] = (double)Math.round(temperatureTotal/plantDataSubset.size() *100d) /100d;
        average[1] = (double)Math.round(humidityTotal/plantDataSubset.size() *100d) /100d;
        average[2] = (double)Math.round(soilMoistureTotal/plantDataSubset.size() *100d) /100d;

        return average;
    }

    /**
     *
     * @return Double length 3 (Temperature Max,
     */
    public double[] getDailyMaximums(){
        int maxDateIndex = getMostRecentDateIndex();
        double[] maxs = new double[3];
        //Get date 24 prior to the maxDate.
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getMostRecentDate());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        List<Integer> plantDataSubset = createPlantDataSubset(new Timestamp(cal.getTimeInMillis()), this.getMostRecentDate());
        double humidityMax = this.humidity.get(plantDataSubset.get(0));
        double temperatureMax = this.temperature.get(plantDataSubset.get(0));;
        double soilMoistureMax = this.soilMoisture.get(plantDataSubset.get(0));;
        for (int i : plantDataSubset) {
            if(this.humidity.get(i) > humidityMax){
                humidityMax = this.humidity.get(i);
            }
            if(this.temperature.get(i) > temperatureMax){
                temperatureMax = this.temperature.get(i);
            }
            if(this.soilMoisture.get(i) > soilMoistureMax){
                soilMoistureMax = this.soilMoisture.get(i);
            }
        }
        maxs[0] = temperatureMax;
        maxs[1] = humidityMax;
        maxs[2] = soilMoistureMax;

        return maxs;
    }

    public double[] getDailyMinimums(){
        int maxDateIndex = getMostRecentDateIndex();
        double[] mins = new double[3];
        //Get date 24 prior to the maxDate.
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getMostRecentDate());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        List<Integer> plantDataSubset = createPlantDataSubset(new Timestamp(cal.getTimeInMillis()), this.getMostRecentDate());
        double humidityMin = this.humidity.get(plantDataSubset.get(0));
        double temperatureMin = this.temperature.get(plantDataSubset.get(0));;
        double soilMoistureMin = this.soilMoisture.get(plantDataSubset.get(0));;
        for (int i : plantDataSubset) {
            if(this.humidity.get(i) < humidityMin){
                humidityMin = this.humidity.get(i);
            }
            if(this.temperature.get(i) < temperatureMin){
                temperatureMin = this.temperature.get(i);
            }
            if(this.soilMoisture.get(i) > soilMoistureMin){
                soilMoistureMin = this.soilMoisture.get(i);
            }
        }
        mins[0] = temperatureMin;
        mins[1] = humidityMin;
        mins[2] = soilMoistureMin;

        return mins;
    }

    public boolean getLowWater(){
        return this.lowWater;
    }

}
