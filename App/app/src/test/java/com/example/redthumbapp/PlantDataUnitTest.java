package com.example.redthumbapp;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.Test;

import java.sql.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PlantDataUnitTest {

    private JSONObject createGoodPlantData(){
        Date date = new Date(1);
        JSONObject plantDataRawGood = new JSONObject();
        plantDataRawGood.put("time", date);
        plantDataRawGood.put("pot_id", new Integer(1));
        plantDataRawGood.put("sunlight", new Boolean(true));
        plantDataRawGood.put("temperature", new Double(23.1));
        plantDataRawGood.put("humidity", new Double(50.6));
        plantDataRawGood.put("soil_moisture", new Double(89.2));
        return plantDataRawGood;
    }
    private JSONObject createGoodPlantData2(Date date){
        JSONObject plantDataRawGood = new JSONObject();
        plantDataRawGood.put("time", date);
        plantDataRawGood.put("pot_id", new Integer(2));
        plantDataRawGood.put("sunlight", new Boolean(false));
        plantDataRawGood.put("temperature", new Double(25.1));
        plantDataRawGood.put("humidity", new Double(55.6));
        plantDataRawGood.put("soil_moisture", new Double(92.2));
        return plantDataRawGood;
    }


    private  JSONObject createNullPlantData(){
        Date date = null;
        JSONObject plantDataRawNULL = new JSONObject();
        plantDataRawNULL.put("time", date);
        plantDataRawNULL.put("pot_id", null);
        plantDataRawNULL.put("sunlight", null);
        plantDataRawNULL.put("temperature", null);
        plantDataRawNULL.put("humidity", null);
        plantDataRawNULL.put("soil_moisture", null);
        return plantDataRawNULL;
    }

    private JSONObject createGoodPlantTypeData(){
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

    @Test
    public void PlantDataFeedTest1(){
        JSONObject[] plantDataArr = new JSONObject[1];
        plantDataArr[0] = createGoodPlantData();

        PlantData plantDataGood = new PlantData(plantDataArr,createGoodPlantTypeData());

        //(sunLight,Humidity,Temperature,SoilMoisture)
        double[] plantDataVals = {(Double)1.0,(Double)50.6,(Double)23.1,(Double)89.2};
        assertEquals(plantDataVals[0],plantDataGood.getFeedData()[0],0.0001);
        assertEquals(plantDataVals[1],plantDataGood.getFeedData()[1],0.0001);
        assertEquals(plantDataVals[2],plantDataGood.getFeedData()[2],0.0001);
        assertEquals(plantDataVals[3],plantDataGood.getFeedData()[3],0.0001);
        assertEquals(1,plantDataGood.getDatapoints());
    }

    @Test(expected = IllegalArgumentException.class)
    public void PlantDataFeedTest2(){
        JSONObject[] plantDataArr = new JSONObject[1];

        plantDataArr[0] = createNullPlantData();

        PlantData plantDataGood = new PlantData(plantDataArr, createGoodPlantTypeData());
        assertEquals(1,plantDataGood.getDatapoints());

        //(sunLight,Humidity,Temperature,SoilMoisture)

    }

    @Test
    public void PlantDataFeedTest3(){
        JSONObject[] plantDataArr = new JSONObject[2];

        plantDataArr[0] = createNullPlantData();

        plantDataArr[1] = createGoodPlantData();

        PlantData plantDataGood = new PlantData(plantDataArr, createGoodPlantTypeData());

        //(sunLight,Humidity,Temperature,SoilMoisture)
        double[] plantDataVals = {(Double)1.0,(Double)50.6,(Double)23.1,(Double)89.2};
        assertEquals(plantDataVals[0],plantDataGood.getFeedData()[0],0.0001);
        assertEquals(plantDataVals[1],plantDataGood.getFeedData()[1],0.0001);
        assertEquals(plantDataVals[2],plantDataGood.getFeedData()[2],0.0001);
        assertEquals(plantDataVals[3],plantDataGood.getFeedData()[3],0.0001);
        assertEquals(1,plantDataGood.getDatapoints());
    }

    @Test
    /**
     * Add a plant after the first good data set. The feed should return this new plantData.
     */
    public void AddPlantDataTest1(){
        JSONObject[] plantDataArr = new JSONObject[2];

        plantDataArr[0] = createNullPlantData();

        plantDataArr[1] = createGoodPlantData();

        PlantData plantDataGood = new PlantData(plantDataArr, createGoodPlantTypeData());

        //(sunLight,Humidity,Temperature,SoilMoisture)
        double[] plantDataVals = {(Double)1.0,(Double)50.6,(Double)23.1,(Double)89.2};
        assertEquals(plantDataVals[0],plantDataGood.getFeedData()[0],0.0001);
        assertEquals(plantDataVals[1],plantDataGood.getFeedData()[1],0.0001);
        assertEquals(plantDataVals[2],plantDataGood.getFeedData()[2],0.0001);
        assertEquals(plantDataVals[3],plantDataGood.getFeedData()[3],0.0001);

        JSONObject[] plantDataArr2 = new JSONObject[1];
        plantDataArr2[0] = createGoodPlantData2(new Date(100));
        plantDataGood.addPlantData(plantDataArr2);


        double[] plantDataVals2 = {(Double)0.0,(Double)55.6,(Double)25.1,(Double)92.2};
        assertEquals(plantDataVals2[0],plantDataGood.getFeedData()[0],0.0001);
        assertEquals(plantDataVals2[1],plantDataGood.getFeedData()[1],0.0001);
        assertEquals(plantDataVals2[2],plantDataGood.getFeedData()[2],0.0001);
        assertEquals(plantDataVals2[3],plantDataGood.getFeedData()[3],0.0001);
        assertEquals(2,plantDataGood.getDatapoints());

    }

    @Test
    /**
     * Add a data set before the first good data set. The plant feed should be the first good data set.
     */
    public void AddPlantDataTest2(){
        JSONObject[] plantDataArr = new JSONObject[2];

        plantDataArr[0] = createNullPlantData();

        plantDataArr[1] = createGoodPlantData();

        PlantData plantDataGood = new PlantData(plantDataArr, createGoodPlantTypeData());

        //(sunLight,Humidity,Temperature,SoilMoisture)
        double[] plantDataVals = {(Double)1.0,(Double)50.6,(Double)23.1,(Double)89.2};
        assertEquals(plantDataVals[0],plantDataGood.getFeedData()[0],0.0001);
        assertEquals(plantDataVals[1],plantDataGood.getFeedData()[1],0.0001);
        assertEquals(plantDataVals[2],plantDataGood.getFeedData()[2],0.0001);
        assertEquals(plantDataVals[3],plantDataGood.getFeedData()[3],0.0001);

        JSONObject[] plantDataArr2 = new JSONObject[1];
        plantDataArr2[0] = createGoodPlantData2(new Date(0));
        plantDataGood.addPlantData(plantDataArr2);

        //Data set should not have changed!
        assertEquals(plantDataVals[0],plantDataGood.getFeedData()[0],0.0001);
        assertEquals(plantDataVals[1],plantDataGood.getFeedData()[1],0.0001);
        assertEquals(plantDataVals[2],plantDataGood.getFeedData()[2],0.0001);
        assertEquals(plantDataVals[3],plantDataGood.getFeedData()[3],0.0001);
        assertEquals(2,plantDataGood.getDatapoints());
    }

    @Test
    /**
     * Add a duplicate data set.
     */
    public void AddPlantDataTest3(){
        JSONObject[] plantDataArr = new JSONObject[1];

        plantDataArr[0] = createGoodPlantData();
        PlantData plantDataGood = new PlantData(plantDataArr, createGoodPlantTypeData());

        //(sunLight,Humidity,Temperature,SoilMoisture)
        double[] plantDataVals = {(Double)1.0,(Double)50.6,(Double)23.1,(Double)89.2};
        assertEquals(plantDataVals[0],plantDataGood.getFeedData()[0],0.0001);
        assertEquals(plantDataVals[1],plantDataGood.getFeedData()[1],0.0001);
        assertEquals(plantDataVals[2],plantDataGood.getFeedData()[2],0.0001);
        assertEquals(plantDataVals[3],plantDataGood.getFeedData()[3],0.0001);


        plantDataGood.addPlantData(plantDataArr);

        //Data set should not have changed!
        assertEquals(plantDataVals[0],plantDataGood.getFeedData()[0],0.0001);
        assertEquals(plantDataVals[1],plantDataGood.getFeedData()[1],0.0001);
        assertEquals(plantDataVals[2],plantDataGood.getFeedData()[2],0.0001);
        assertEquals(plantDataVals[3],plantDataGood.getFeedData()[3],0.0001);
        assertEquals(1,plantDataGood.getDatapoints());
    }

    @Test
    public void GetMostRecentDateTest1(){
        JSONObject[] plantDataArr = new JSONObject[3];

        plantDataArr[1] = createGoodPlantData2(new Date(100));
        plantDataArr[0] = createGoodPlantData2(new Date(1));
        plantDataArr[2] = createGoodPlantData2(new Date(99));
        PlantData plantDataGood = new PlantData(plantDataArr, createGoodPlantTypeData());

        assertEquals(new Date(100),plantDataGood.getMostRecentDate());
    }

    @Test
    public void FeedQualitiesTest1(){
        JSONObject[] plantDataArr = new JSONObject[3];

        plantDataArr[1] = createGoodPlantData2(new Date(100));
        plantDataArr[0] = createGoodPlantData2(new Date(1));
        plantDataArr[2] = createGoodPlantData2(new Date(99));
        PlantData plantDataGood = new PlantData(plantDataArr, createGoodPlantTypeData());
        plantDataGood.getFeedDataQualities();

    }

    @Test
    public void HistoricalFeedTest1(){
        JSONObject[] plantDataArr = new JSONObject[3];

        plantDataArr[1] = createGoodPlantData2(new Date(100));
        plantDataArr[0] = createGoodPlantData2(new Date(1));
        plantDataArr[2] = createGoodPlantData2(new Date(99));
        PlantData plantDataGood = new PlantData(plantDataArr, createGoodPlantTypeData());
        Map<Date,Double[]> historicalPlantDataMap = plantDataGood.getHistoricalFeed(new Date(2), new Date(101));
        assertEquals(2,historicalPlantDataMap.size());

    }

    @Test(expected = IllegalArgumentException.class)
    public void HistoricalFeedTest2(){
        JSONObject[] plantDataArr = new JSONObject[3];

        plantDataArr[1] = createGoodPlantData2(new Date(100));
        plantDataArr[0] = createGoodPlantData2(new Date(1));
        plantDataArr[2] = createGoodPlantData2(new Date(99));
        PlantData plantDataGood = new PlantData(plantDataArr, createGoodPlantTypeData());
        Map<Date,Double[]> historicalPlantDataMap = plantDataGood.getHistoricalFeed(new Date(101), new Date(2));
    }

}