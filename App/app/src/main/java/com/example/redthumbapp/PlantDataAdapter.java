package com.example.redthumbapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class PlantDataAdapter extends RecyclerView.Adapter<PlantDataAdapter.ViewHolder>{

    @Override
    public PlantDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.plant_data_feed_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }
    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PlantDataAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        PlantFeedData plantData = plantDataList.get(position);

        // Set item views based on your views and data model
        TextView textPlantID = viewHolder.textPlantID;
        textPlantID.setText("Pot ID: " + plantData.getPotID());
        TextView textPlantType = viewHolder.textPlantType;
        textPlantType.setText(plantData.getPlantType());
        TextView textSunlight = viewHolder.textSunlight;
        textSunlight.setText("Sunlight: " + (plantData.getSunlight() == 1.0 ? "Bright" : "Dark"));
        TextView textTemperature = viewHolder.textTemperature;

        textTemperature.setText("Temperature: " + (double)Math.round(plantData.getTemperature() * 100d) / 100d + "°C");
        TextView textHumidity = viewHolder.textHumidity;
        textHumidity.setText("Humidity: " + (double)Math.round(plantData.getHumidity() * 100d) / 100d + "%");
        TextView textSoilMoisture = viewHolder.textSoilMoisture;
        textSoilMoisture.setText("Soil Moisture: " + (double)Math.round(plantData.getSoilMoisture() * 100d) / 100d + "%");
        ImageView imagePlant = viewHolder.imagePlant;
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return plantDataList.size();
    }

// Provide a direct reference to each of the views within a data item
// Used to cache the views within the item layout for fast access
public class ViewHolder extends RecyclerView.ViewHolder {
    // Your holder should contain a member variable
    // for any view that will be set as you render a row
    public TextView textPlantID;
    public TextView textPlantType;
    public TextView textSunlight;
    public TextView textTemperature;
    public TextView textHumidity;
    public TextView textSoilMoisture;
    public ImageView imagePlant;

    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    public ViewHolder(View itemView) {
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
        super(itemView);

        textPlantID = (TextView) itemView.findViewById(R.id.textPotID);
        textPlantType = (TextView) itemView.findViewById(R.id.textPlantType);
        textSunlight = (TextView) itemView.findViewById(R.id.textSunlight);
        textTemperature = (TextView) itemView.findViewById(R.id.textTemperature);
        textHumidity = (TextView) itemView.findViewById(R.id.textHumidity);
        textSoilMoisture = (TextView) itemView.findViewById(R.id.textSoilMoisture);
        imagePlant = (ImageView) itemView.findViewById(R.id.plantImageView);
        imagePlant.setImageResource(R.drawable.ic_sprout);
    }
}
    // Store a member variable for the contacts
    private List<PlantFeedData> plantDataList;

    // Pass in the contact array into the constructor
    public PlantDataAdapter(List<PlantFeedData> plantDataList) {
        this.plantDataList = plantDataList;
    }

}
