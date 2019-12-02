package com.example.redthumbapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Collections;
import java.util.List;

public class MessageSender extends AsyncTask<String, Void, Void>{

    private DatagramSocket s;
    private DatagramSocket socket;

    private final static int PACKETSIZE = 100;

    /*

     */
    @Override
    protected Void doInBackground(String... voids){

        String request = voids[0];
        try
        {
            int port = 11616;
            InetAddress hubIP = InetAddress.getByName("10.0.0.71");

            s = new DatagramSocket(port);

            // Send Request
            request = new String(getIPAddress(true)) + " " + request;
            byte [] data = request.getBytes();
            DatagramPacket sendPacket = new DatagramPacket( data, data.length, hubIP, port ) ;
            s.send(sendPacket);

            // Receive response
            socket = new DatagramSocket(port);
            DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
            socket.receive(packet);

            // Response into a String
            String str = new String(packet.getData());
            JSONArray jsonArray = new JSONArray(str);

            //Receive all the pot data using pot_ids
            for(int i = 0; i < jsonArray.length(); i++){
                int pot_id = jsonArray.getJSONObject(i).getInt("pot_id");
                request = "requestPlantType";
                request = new String(getIPAddress(true)) + " " + request+ " " + pot_id;
                data = request.getBytes();
                sendPacket = new DatagramPacket( data, data.length, hubIP, port );
                s.send(sendPacket);

            }

//            // Formulating received Data
//            if (request.equals("requestPots")){
//                JSONArray jsonArray = new JSONArray(str);
//            }
//            else if(request.equals("requestPlantType")){
//                JSONObject jsonObject = new JSONObject(str);
//            }
//            else if(request.equals("requestAllPlantTypes")){
//                JSONArray jsonArray = new JSONArray(str);
//            }
//            else if(request.equals("requestPotCurrentData")){
//                JSONObject jsonObject = new JSONObject(str);
//            }
//            else if(request.equals("requestPotRecentData")){
//                JSONArray jsonArray = new JSONArray(str);
//            }
//            else if(request.equals("requestCompleteDataPot")){
//                JSONArray jsonArray = new JSONArray(str);
//            }
//            else{
//                //Receive acknowledge message
//            }

            s.close();
            socket.close();

        }
        catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //this code was found on https://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device-from-code
    private static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }

}
