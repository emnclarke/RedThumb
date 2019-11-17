package com.example.redthumbapp;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.Test;

import java.sql.Date;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PlantDataUnitTest {

    @Test
    public void PlantDataFeedTest1() throws JSONException {
        JSONObject[] plantDataArr = new JSONObject[1];

        Date date = new Date(1);

        JSONObject plantDataRawGood = new JSONObject();
        plantDataRawGood.put("time", date);
        plantDataRawGood.put("pot_id", new Integer(1));
        plantDataRawGood.put("sunlight", new Boolean(true));
        plantDataRawGood.put("temperature", new Double(23.1));
        plantDataRawGood.put("humidity", new Double(50.6));
        plantDataRawGood.put("soil_moisture", new Double(89.2));
        plantDataArr[0] = plantDataRawGood;

        //Create dummy object PlantTypeData
        JSONObject plantTypeDataGood = new JSONObject();
        plantTypeDataGood.put("plant_type", 1);
        plantTypeDataGood.put("name", "Testhead Palm");
        plantTypeDataGood.put("sun_coverage", new Double(8));
        plantTypeDataGood.put("temperature", new Double(23));
        plantTypeDataGood.put("humidity", new Double(50));
        plantTypeDataGood.put("soil_moisture", new Double(90));

        PlantData plantDataGood = new PlantData(plantDataArr,plantTypeDataGood);

        //(sunLight,Humidity,Temperature,SoilMoisture)
        double[] plantDataVals = {(Double)1.0,(Double)50.6,(Double)23.1,(Double)89.2};
        assertEquals(plantDataVals[0],plantDataGood.getFeedData()[0],0.0001);
        assertEquals(plantDataVals[1],plantDataGood.getFeedData()[1],0.0001);
        assertEquals(plantDataVals[2],plantDataGood.getFeedData()[2],0.0001);
        assertEquals(plantDataVals[3],plantDataGood.getFeedData()[3],0.0001);
    }


}