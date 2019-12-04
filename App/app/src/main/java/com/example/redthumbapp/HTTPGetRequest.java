package com.example.redthumbapp;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
        System.out.println(data);
        super.onPostExecute(data);
        //TODO:
    }
}
