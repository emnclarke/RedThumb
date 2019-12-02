package com.example.redthumbapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String HTTPResponseString;
    ArrayList<PlantFeedData> plantFeed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load plantFeed
        //Attempt to connect to the web server


        //Attempt to load view based on plantFeed
        loadPlantDataView();

        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                loadPlantDataView();
            }
        });

    }

    private void loadPlantData(){
    //Load plant data
        HTTPGetRequest request = new HTTPGetRequest();
        request.execute("/?request=pot");


    }
    private void reloadPlantData(){
        //Force a load of plant Data


    }
    private void loadPlantDataView(){

        RecyclerView plantDataRV = (RecyclerView) findViewById(R.id.plantDataRV);

        //Attempt to load plant data;
        loadPlantData();
        if(plantFeed != null) {
            PlantDataAdapter adapter = new PlantDataAdapter(plantFeed);
            plantDataRV.setAdapter(adapter);
            plantDataRV.setLayoutManager(new LinearLayoutManager(this));
        }else{
            //No plant data loaded case

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.loadFakeData:
                plantFeed = PlantFeedData.createDummyPlants(12);
                loadPlantDataView();
                break;
            case R.id.forceLoadData:
                reloadPlantData();
                loadPlantDataView();

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(View v) {
    }

    public class HTTPGetRequest extends AsyncTask<String, String, String> {
        private static final String HUB_SERVER = "http://192.168.43.85:3000//";//FIXME:
        static final String REQUEST_METHOD = "GET";
        static final int READ_TIMEOUT = 5000;
        static final int CONNECTION_TIMEOUT = 15000;


        @Override
        protected String doInBackground(String... request) {
            String data;
            String inputLine;

            try{
                //Connect to server
                URL hubURL = new URL(HUB_SERVER + request);
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
            HTTPResponseString = data;
            super.onPostExecute(data);
        }
    }

}

