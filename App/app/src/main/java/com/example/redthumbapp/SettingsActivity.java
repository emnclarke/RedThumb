package com.example.redthumbapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Text;

public class SettingsActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText tempInput;
    private EditText humidInput;
    private EditText soilMoistInput;
    private EditText sunCoverageInput;
    private EditText waterFreqInput;
    private SeekBar waterIntensityInput;
    TextView waterIntensityVal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nameInput = findViewById(R.id.plantTypeNameInput);
        tempInput = findViewById(R.id.plantTypeTemperatureInput);
        humidInput = findViewById(R.id.plantTypeHumidityInput);
        soilMoistInput = findViewById(R.id.plantTypeSoilMoistureInput);
        sunCoverageInput = findViewById(R.id.plantTypeSunCoverageInput);
        waterFreqInput = findViewById(R.id.plantTypeWaterFreqInput);
        waterIntensityInput = findViewById(R.id.plantTypeWaterIntensityInput);
        waterIntensityVal = findViewById(R.id.waterIntensityVal);


        final Button createPlantTypeButton = findViewById(R.id.createPlantType);
        createPlantTypeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createPlantType();
            }
        });

        waterIntensityInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                waterIntensityVal.setText("" + progress + " Minutes");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void createPlantType(){
        JSONObject plantType = new JSONObject();
        plantType.put("name",nameInput.getText());
        plantType.put("temperature",tempInput.getText());
        plantType.put("humidity",humidInput.getText());
        plantType.put("soil_moisture",soilMoistInput.getText());
        plantType.put("sun_coverage",sunCoverageInput.getText());
        plantType.put("water_frequency",waterFreqInput.getText());
        plantType.put("water_length",waterIntensityInput.getProgress());

        //Send Plant Type to Database!
        
    }
}
