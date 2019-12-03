package com.example.redthumbapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlantDataAdapter extends RecyclerView.Adapter<PlantDataAdapter.ViewHolder> {

    @Override
    public PlantDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {

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
        textPlantID.setText(plantData.getPotID());
        TextView textPlantType = viewHolder.textPlantType;
        textPlantType.setText(plantData.getPlantType());

        TextView textSunlight = viewHolder.textSunlight;
        TextView textTemperature = viewHolder.textTemperature;
        TextView textHumidity = viewHolder.textHumidity;
        TextView textSoilMoisture = viewHolder.textSoilMoisture;


        //If Plant has no data:
        if(plantDataList.get(position).isData()) {

            textSunlight.setText((plantData.getSunlight() == 1.0 ? "Bright" : "Dark"));
            textTemperature.setText((double) Math.round(plantData.getTemperature() * 100d) / 100d + "°C");
            textHumidity.setText((double) Math.round(plantData.getHumidity() * 100d) / 100d + "%");
            textSoilMoisture.setText((double) Math.round(plantData.getSoilMoisture() * 100d) / 100d + "%");
        }else{
            textSunlight.setText("No data");
            textTemperature.setText("No data");
            textHumidity.setText("No data");
            textSoilMoisture.setText("No Data");
        }

        //Find a plant Icon based on the PotID
        //This means a pot will always have the same icon
        ImageView imagePlant = viewHolder.imagePlant;
        int plantIcon = (Integer.valueOf(plantData.getPotIDInteger()) % 11);
        switch(plantIcon) {
            case 0:
                imagePlant.setImageResource(R.drawable.ic_plant_0);
                break;
            case 1:
                imagePlant.setImageResource(R.drawable.ic_plant_1);
                break;
            case 2:
                imagePlant.setImageResource(R.drawable.ic_plant_2);
                break;
            case 3:
                imagePlant.setImageResource(R.drawable.ic_plant_3);
                break;
            case 4:
                imagePlant.setImageResource(R.drawable.ic_plant_4);
                break;
            case 5:
                imagePlant.setImageResource(R.drawable.ic_plant_5);
                break;
            case 6:
                imagePlant.setImageResource(R.drawable.ic_plant_6);
                break;
            case 7:
                imagePlant.setImageResource(R.drawable.ic_plant_7);
                break;
            case 8:
                imagePlant.setImageResource(R.drawable.ic_plant_8);
                break;
            case 9:
                imagePlant.setImageResource(R.drawable.ic_plant_9);
                break;
            case 10:
                imagePlant.setImageResource(R.drawable.ic_plant_10);
                break;
        }
        ProgressBar progressBarSunlight = viewHolder.progressBarSunlight;

        progressBarSunlight.setMax(100);
        progressBarSunlight.setProgress((int) plantData.getSunlightQuality());
//        System.out.println("Sunlight Quality: " + plantData.getSunlightQuality());

        ProgressBar progressBarTemperature = viewHolder.progressBarTemperature;
        progressBarTemperature.setMax(100);
        progressBarTemperature.setProgress((int) plantData.getTemperatureQuality());
//        System.out.println("Temperature Quality: " + plantData.getTemperatureQuality());

        ProgressBar progressBarHumidity = viewHolder.progressBarHumidity;
        progressBarHumidity.setMax(100);
        progressBarHumidity.setProgress((int) plantData.getHumidityQuality());
//        System.out.println("Humidity Quality: " + plantData.getHumidityQuality());

        ProgressBar progressBarSoilMoisture = viewHolder.progressBarSoilMositure;
        progressBarSoilMoisture.setMax(100);
        progressBarSoilMoisture.setProgress((int) plantData.getSoilMoistureQuality());
//        System.out.println("Soil Moisture Quality: " + plantData.getSoilMoistureQuality());

        ImageButton historyButton = viewHolder.historyButton;




    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return plantDataList.size();
    }

    // Provide a direct reference to each of the views within a data item
// Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView textPlantID;
        public TextView textPlantType;
        public TextView textSunlight;
        public TextView textTemperature;
        public TextView textHumidity;
        public TextView textSoilMoisture;
        public ImageView imagePlant;
        //Progress Bars
        public ProgressBar progressBarSunlight;
        public ProgressBar progressBarTemperature;
        public ProgressBar progressBarHumidity;
        public ProgressBar progressBarSoilMositure;

        //Button
        public ImageButton historyButton;

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
            historyButton = (ImageButton) itemView.findViewById(R.id.historyButton);

            progressBarSunlight = (ProgressBar) itemView.findViewById(R.id.progressBarSunlight);
            progressBarTemperature = (ProgressBar) itemView.findViewById(R.id.progressBarTemperature);
            progressBarHumidity = (ProgressBar) itemView.findViewById(R.id.progressBarHumidity);
            progressBarSoilMositure = (ProgressBar) itemView.findViewById(R.id.progressBarSoilMoisture);

            historyButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

//            if(!plantDataList.get(position).isData()){
//                Context context = v.getContext();
//                CharSequence text = "No data available";
//                int duration = Toast.LENGTH_SHORT;
//
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();
//                return;
//            }
            Intent intent = new Intent (v.getContext(), HistoricalPlantView.class);
            //Title Block
            intent.putExtra("plantTitle",plantDataList.get(position).getPotID());
            intent.putExtra("plantType",plantDataList.get(position).getPlantType());

            //Sunlight Data
            if(plantDataList.get(position).isData()) {
                intent.putExtra("dailySunlightHours", Double.toString(plantDataList.get(position).plantData.getFeedDataQualities()[4]));
                intent.putExtra("dailySunlightHoursReq", ((Long) plantDataList.get(position).plantData.getPlantTypeData().get("sun_coverage")).toString());
                //Calculate Quality Index for display
                intent.putExtra("sunlightQualityIndex", getIndexQualityString(plantDataList.get(position).getSunlightQuality()));

                //Temperature Data
                intent.putExtra("temperatureAverage", Double.toString(plantDataList.get(position).plantData.getDailyAverages()[0]));
                intent.putExtra("temperatureMax", Double.toString(plantDataList.get(position).plantData.getDailyMaximums()[0]));
                intent.putExtra("temperatureMin", Double.toString(plantDataList.get(position).plantData.getDailyMinimums()[0]));
                intent.putExtra("idealTemperature", ((Double) plantDataList.get(position).plantData.getPlantTypeData().get("temperature")).toString());
                intent.putExtra("temperatureQuality", getIndexQualityString(plantDataList.get(position).getTemperatureQuality()));

                //Humidity Data
                intent.putExtra("humidityAverage", Double.toString(plantDataList.get(position).plantData.getDailyAverages()[1]));
                intent.putExtra("humidityMax", Double.toString(plantDataList.get(position).plantData.getDailyMaximums()[1]));
                intent.putExtra("humidityMin", Double.toString(plantDataList.get(position).plantData.getDailyMinimums()[1]));
                intent.putExtra("idealHumidity", ((Double) plantDataList.get(position).plantData.getPlantTypeData().get("humidity")).toString());
                intent.putExtra("humidityQuality", getIndexQualityString(plantDataList.get(position).getHumidityQuality()));

                //Soil Moisture Data
                intent.putExtra("soilMoistureAverage", Double.toString(plantDataList.get(position).plantData.getDailyAverages()[2]));
                intent.putExtra("soilMoistureMax", Double.toString(plantDataList.get(position).plantData.getDailyMaximums()[2]));
                intent.putExtra("soilMoistureMin", Double.toString(plantDataList.get(position).plantData.getDailyMinimums()[2]));
                intent.putExtra("idealSoilMoisture", (plantDataList.get(position).plantData.getPlantTypeData().get("soil_moisture")).toString());
                intent.putExtra("soilMoistureQuality", getIndexQualityString(plantDataList.get(position).getSoilMoistureQuality()));

                //Pot Data
                intent.putExtra("lastWatered", (plantDataList.get(position).getLastWatered() == null) ? "Never" :plantDataList.get(position).getLastWatered());
            }
            intent.putExtra("pot_id_int",plantDataList.get(position).getPotIDInteger());
            v.getContext().startActivity(intent);
        }
    }

    // Store a member variable for the contacts
    private List<PlantFeedData> plantDataList;

    // Pass in the contact array into the constructor
    public PlantDataAdapter(List<PlantFeedData> plantDataList) {
        this.plantDataList = plantDataList;
    }

    private String getIndexQualityString(double quality){
        if(quality >= 100.0){
            return "Perfect";
        }
        if(quality >= 90.0){
            return "Great";
        }
        if(quality >= 75.0){
            return "Good";
        }
        if(quality >= 50.0){
            return "Fair";
        }
        else{
            return "Poor";
        }
    }
}
