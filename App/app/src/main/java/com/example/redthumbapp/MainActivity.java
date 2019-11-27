package com.example.redthumbapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<PlantFeedData> plantFeed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView plantDataRV = (RecyclerView) findViewById(R.id.plantDataRV);

        // Initialize contacts
        plantFeed = PlantFeedData.createDummyPlants(1);
        // Create adapter passing in the sample user data
         PlantDataAdapter adapter = new PlantDataAdapter(plantFeed);
        // Attach the adapter to the recyclerview to populate items
        plantDataRV.setAdapter(adapter);
        // Set layout manager to position the items
        plantDataRV.setLayoutManager(new LinearLayoutManager(this));
        // That's all!
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
                //TODO:Navigate to settings screen
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

}
