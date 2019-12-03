package com.example.redthumbapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    int requestFlag = 0;
    ArrayList<String> plantTypes;
    ArrayList<Integer> plantIDs;
    private EditText nameInput;
    private SeekBar tempInput;
    private SeekBar humidInput;
    private SeekBar soilMoistInput;
    private SeekBar sunCoverageInput;
    private SeekBar waterFreqInput;
    private SeekBar waterIntensityInput;
    TextView tempInputVal;
    TextView humidInputVal;
    TextView moistInputVal;
    TextView sunCoverageInputVal;
    TextView waterIntensityVal;
    TextView waterFreqVal;
    TextView plantTypesVal;

    EditText potName;
    EditText potIP;
    SeekBar plantTypeSeeker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        plantTypes = new ArrayList<String>();
        plantIDs = new ArrayList<Integer>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nameInput = findViewById(R.id.plantTypeNameInput);
        tempInputVal = findViewById(R.id.plantTypeTempVal);
        tempInput = findViewById(R.id.plantTypeTempInput);
        humidInputVal = findViewById(R.id.plantTypeHumidVal);
        humidInput = findViewById(R.id.plantTypeHumidInput);
        moistInputVal = findViewById(R.id.plantTypeMoistVal);
        soilMoistInput = findViewById(R.id.plantTypeSoilMoistureInput);
        sunCoverageInputVal = findViewById(R.id.plantTypeSunCoverageVal);
        sunCoverageInput = findViewById(R.id.plantTypeSunCoverageInput);

        waterFreqInput = findViewById(R.id.plantTypeWaterFreqInput);
        waterFreqVal = findViewById(R.id.plantTypeWaterFreqVal);
        waterIntensityInput = findViewById(R.id.plantTypeWaterIntensityInput);
        waterIntensityVal = findViewById(R.id.plantTypeWaterIntensityVal);

        plantTypesVal = findViewById(R.id.plantTypesVal);

        plantTypeSeeker = findViewById(R.id.plantTypesInput);
        //Load plant Type List
        HTTPGetRequest requestTypeList = new HTTPGetRequest();
        requestTypeList.execute("?request=requestAllPlantTypes");

        //Create pot

        potName = findViewById(R.id.potName);
        potIP = findViewById(R.id.potIP);





        Button createPlantTypeButton = findViewById(R.id.createPlantType);
        createPlantTypeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createPlantType();
            }
        });



        Button createNewPotButton = findViewById(R.id.createPot);
        createNewPotButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createNewPot();
            }
        });
        tempInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                tempInputVal.setText("" + progress + " °C");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        humidInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                humidInputVal.setText("" + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        soilMoistInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                if(progress == 0){
                    moistInputVal.setText("Dry");
                }else if(progress == 1){
                    moistInputVal.setText("Wet");
                }else{
                    moistInputVal.setText("Very Wet");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sunCoverageInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                sunCoverageInputVal.setText("" + progress + " Hours");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        plantTypeSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                System.out.println(progress);
                if(plantTypes.size() > progress) {
                    plantTypesVal.setText(plantTypes.get(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        waterFreqInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                waterFreqVal.setText("Every " + progress + " Days");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        waterIntensityInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                waterIntensityVal.setText("" + progress + " Seconds");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }



    private void createNewPot() {
        HTTPGetRequest requestCreatePot = new HTTPGetRequest();
        String potNameText = potName.getText().toString();
        String potIPText = potIP.getText().toString() ;
        String potPlantType = String.valueOf(plantIDs.get(plantTypeSeeker.getProgress()));
        System.out.println(String.format("?request=addPot&arg1=%s&arg2=%s&arg3=%s&arg4=null&arg5=null&arg6=null",potNameText,potIPText,potPlantType));
        requestCreatePot.execute(String.format("?request=addPot&arg1=%s&arg2=%s&arg3=%s&arg4=null&arg5=null&arg6=null",potNameText,potIPText,potPlantType));
        //Send Plant Type to Database!
        Context context = getApplicationContext();
        CharSequence text = potNameText + " pot added";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();


    }

    private void editPlantType() {
        String name = nameInput.getText().toString();
        String waterFreq = String.valueOf(waterFreqInput.getProgress());
        String waterLength = String.valueOf(waterIntensityInput.getProgress());
        String temp = String.valueOf(tempInput.getProgress());
        String humidity = String.valueOf(humidInput.getProgress());
        String soilMoisture = moistInputVal.getText().toString().toLowerCase();
        String sunCoverage = String.valueOf(sunCoverageInput.getProgress());
        HTTPGetRequest request = new HTTPGetRequest();

    }

    private void createPlantType(){
        String name = nameInput.getText().toString();
        String waterFreq = String.valueOf(waterFreqInput.getProgress());
        String waterLength = String.valueOf(waterIntensityInput.getProgress());
        String temp = String.valueOf(tempInput.getProgress());
        String humidity = String.valueOf(humidInput.getProgress());
        String soilMoisture = moistInputVal.getText().toString().toLowerCase();
        String sunCoverage = String.valueOf(sunCoverageInput.getProgress());
        HTTPGetRequest request = new HTTPGetRequest();
        request.execute(String.format("?request=addPlantType&arg1=%s&arg2=%s&arg3=%s&arg4=%s&arg5=%s&arg6=%s&arg7=%s",name,waterFreq,waterLength,temp,humidity,soilMoisture,sunCoverage));
        //Send Plant Type to Database!
        Context context = getApplicationContext();
        CharSequence text = name + " plant type added";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        clearPlantTypeInput();

        
    }

    private void clearPlantTypeInput() {
        nameInput.setText("");
        tempInput.setProgress(0);
        humidInput.setProgress(0);
        soilMoistInput.setProgress(0);
        sunCoverageInput.setProgress(0);
        waterFreqInput.setProgress(0);
        waterIntensityInput.setProgress(0);
    }

    public class HTTPGetRequest extends AsyncTask<String, String, String> {
        private static final String HUB_SERVER = "http://192.168.43.85:3000/";//FIXME:
        static final String REQUEST_METHOD = "GET";
        static final int READ_TIMEOUT = 5000;
        static final int CONNECTION_TIMEOUT = 15000;


        @Override
        protected String doInBackground(String... request) {
            String data;
            String inputLine;

            try{
                //Connect to server
                URL hubURL = new URL(HUB_SERVER + request[0]);
                HttpURLConnection connection = (HttpURLConnection) hubURL.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.connect();

                //Get data
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while((inputLine = bufferedReader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                bufferedReader.close();
                streamReader.close();
                data = stringBuilder.toString();

            }catch (IOException e){
                data = "error";
            }
            return data;
        }

        protected void onPostExecute(String data){
            httpResponse(data);
            super.onPostExecute(data);
            //TODO:
        }
    }

    public void httpResponse(String data) {
        if (data.equals("error")) {
            Context context = getApplicationContext();
            CharSequence text = "Data could not be loaded";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        } else {
            //Expecting pot List
            try {
                if (data.endsWith("end")) {
                    data = data.substring(0, data.length() - 3);
                } else {
                    System.out.println("Data is truncated!");
                    return;
                }
                org.json.simple.parser.JSONParser parser = new JSONParser();
                Object p = parser.parse(data);
                if (p instanceof org.json.simple.JSONArray) {
//                        System.out.println("JSONArray");
                    org.json.simple.JSONArray object = (JSONArray) p;
                    if (requestFlag == 0) {
                        getPlantTypeList(object);
                        return;
                    }
                    if (requestFlag == 1) {
                        return;
                    }
                } else if (p instanceof org.json.simple.JSONObject) {
//                        System.out.println("JSONObject");
                    org.json.simple.JSONObject object = (JSONObject) p;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return;
        }

    }

    private void getPlantTypeList(JSONArray JArr) {
        requestFlag = 1;
        for(Object plantType : JArr){
            JSONObject JObj = (JSONObject) plantType;
            plantTypes.add((String) JObj.get("name"));
            plantIDs.add((Math.toIntExact((Long) JObj.get("plant_id"))));
        }
        System.out.println("Seeker max = " + plantTypes.size());
        plantTypeSeeker.setMax(plantTypes.size()-1);
    }



}
