package com.example.redthumbapp;

import android.os.AsyncTask;

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


    @Override
    protected Void doInBackground(String... voids){

        String message = voids[0];
        try
        {
//            InetAddress localhost = InetAddress.getByName("localhost");

            int port = 11616;
            InetAddress hubIP = InetAddress.getByName("10.0.0.71");

            s = new DatagramSocket();

            // Send Request
            message = new String(getIPAddress(true)) + " " + message;
            byte [] data = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket( data, data.length, hubIP, port ) ;
            s.send(sendPacket);

            // Receive response
            socket = new DatagramSocket(11616);
            DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
            socket.receive(packet);
            System.out.println(new String(packet.getData()).trim());

            s.close();
            socket.close();

        }
        catch(IOException e){
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
