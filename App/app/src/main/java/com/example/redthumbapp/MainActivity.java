package com.example.redthumbapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<PlantFeedData> plantFeed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load plantFeed

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
        MessageSender messageSender = new MessageSender();
        String message = "requestPots";
        //Send Data Request
        messageSender.execute(message);


    }
    private void reloadPlantData(){
        //Force a load of plant Data
        MessageSender messageSender = new MessageSender();
        String message = "requestPots";
        //Send Data Request
        messageSender.execute(message);

    }
    private void loadPlantDataView(){
        RecyclerView plantDataRV = (RecyclerView) findViewById(R.id.plantDataRV);

        //Attempt to load plant data;

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


}
