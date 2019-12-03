package com.example.redthumbapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class HistoricalPlantView extends AppCompatActivity {
    private String potID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_plant_view);
        Intent intent = getIntent();

        potID = intent.getStringExtra("pot_id_int");
        //Set up title Block
        TextView plantName = findViewById(R.id.plantNameHistorical);
        plantName.setText(intent.getStringExtra("plantTitle"));

        TextView plantType = findViewById(R.id.plantTypeHistorical);
        plantType.setText(intent.getStringExtra("plantType"));

        //Sunlight Information Block
        TextView dailyHours = findViewById(R.id.sunlightHoursData);
        dailyHours.setText(intent.getStringExtra("dailySunlightHours"));

        TextView dailyHoursReq = findViewById(R.id.sunlightHoursReqData);
        dailyHoursReq.setText(intent.getStringExtra("dailySunlightHoursReq"));

        TextView sunlightQuality = findViewById(R.id.sunlightHoursQualityData);
        sunlightQuality.setText(intent.getStringExtra("sunlightQualityIndex"));

        //Temperature Information Block
        TextView averageTemperature = findViewById(R.id.temperatureAverageData);
        averageTemperature.setText(intent.getStringExtra("temperatureAverage"));

        TextView maximumTemperature = findViewById(R.id.temperatureMaxData);
        maximumTemperature.setText(intent.getStringExtra("temperatureMax"));

        TextView minimumTemperature = findViewById(R.id.temperatureMinData);
        minimumTemperature.setText(intent.getStringExtra("temperatureMin"));

        TextView idealTemperature = findViewById(R.id.temperatureIdealData);
        idealTemperature.setText(intent.getStringExtra("idealTemperature"));

        TextView temperatureQuality = findViewById(R.id.temperatureQualityData);
        temperatureQuality.setText(intent.getStringExtra("temperatureQuality"));

        //Humidity Information Block
        TextView averageHumidity = findViewById(R.id.humidityAverageData);
        averageHumidity.setText(intent.getStringExtra("humidityAverage"));

        TextView maximumHumidity = findViewById(R.id.humidityMaxData);
        maximumHumidity.setText(intent.getStringExtra("humidityMax"));

        TextView minimumHumidity = findViewById(R.id.humidityMinData);
        minimumHumidity.setText(intent.getStringExtra("humidityMin"));

        TextView idealHumidity = findViewById(R.id.humidityIdealData);
        idealHumidity.setText(intent.getStringExtra("idealHumidity"));

        TextView humidityQuality = findViewById(R.id.humidityQualityData);
        humidityQuality.setText(intent.getStringExtra("humidityQuality"));

        //Soil Moisture Information Block
        TextView averageSoilMoisture = findViewById(R.id.soilMoistureAverageData);
        averageSoilMoisture.setText(intent.getStringExtra("soilMoistureAverage"));

        TextView maximumSoilMoisture = findViewById(R.id.soilMoistureMaxData);
        maximumSoilMoisture.setText(intent.getStringExtra("soilMoistureMax"));

        TextView minimumSoilMoisture = findViewById(R.id.soilMoistureMinData);
        minimumSoilMoisture.setText(intent.getStringExtra("soilMoistureMin"));

        TextView idealSoilMoisture = findViewById(R.id.soilMoistureIdealData);
        idealSoilMoisture.setText(intent.getStringExtra("idealSoilMoisture"));

        TextView soilMositureQuality = findViewById(R.id.soilMoistureQualityData);
        soilMositureQuality.setText(intent.getStringExtra("soilMoistureQuality"));

        TextView waterLastWateredData = findViewById(R.id.waterLastWateredData);
        waterLastWateredData.setText(intent.getStringExtra("lastWatered"));

        Button forceWaterButton = findViewById(R.id.forceWaterButton);
        forceWaterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                forceWaterNow();
            }
        });

        Button deletePotButton = findViewById(R.id.deletePotButton);
        deletePotButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deletePot();
                finish();
            }
        });
    }

    public void deletePot(){
        HTTPGetRequest request = new HTTPGetRequest();
        request.execute(String.format("?request=deletePot&arg1=%s",potID));
    }
    public void forceWaterNow()
    {
        HTTPGetRequest request = new HTTPGetRequest();
        request.execute(String.format("?request=updatePot&arg1=%s&arg2=null&arg3=null&arg4=null&arg5=null&arg6=null&arg7=1",potID));

    }

}
