package com.arnav.pocdoc.maps;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ApiQuery {
    private static String my_api_key="AIzaSyC2Tv4y-8BukviK1wAAHyDprfbFB8xaj6A";
    private static Location location;
    private static StringBuffer sbuff;
    private static String cat;
    private static ArrayList<HospitalDetails> hospdetails;

    public static ArrayList<HospitalDetails> ping(Location loc,String category)
    {
        location=loc;
        cat=category;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ApiQuery.query();
            }
        });

        thread.start();

        while(thread.isAlive());

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                ApiQuery.stringToObjects();
            }
        });

        thread.start();

        while(thread.isAlive());

        return hospdetails;
    }

    public static void query()
    {
        String q;

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("types="+cat);
        sb.append("&location="+location.getLatitude()+","+location.getLongitude());
        sb.append("&radius=3000");
        sb.append("&key="+my_api_key);

        q=sb.toString();

        try {
            URL url = new URL(q);
            HttpURLConnection httpconn = (HttpURLConnection)url.openConnection();
            InputStream inputStream = httpconn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buff = new StringBuffer();
            String s="";

            while((s=br.readLine())!=null)
            {
                buff.append(s);
            }

            sbuff=buff;
            return;
        }
        catch(Exception e) {
            Log.d("url exception", e.toString());
        }

        sbuff=null;
    }

    public static void stringToObjects() {
        hospdetails = new ArrayList();

        if (sbuff == null)
        {
            hospdetails=null;
            return;
        }

        try {
            JSONObject jsonobj = new JSONObject(sbuff.toString());
            JSONArray jsonarray = jsonobj.getJSONArray("results");

            for(int i=0; i<jsonarray.length(); i++){

                HospitalDetails hospitalDetails = new HospitalDetails();

                JSONObject jsonObject = jsonarray.getJSONObject(i);

                if(jsonObject.getString("name")!=null)  hospitalDetails.setHospitalName(jsonObject.getString("name"));
                else  hospitalDetails.setHospitalName("Not Available");

                try {
                    hospitalDetails.setRating(String.valueOf(jsonObject.getDouble("rating")));
                }catch (Exception e){
                    hospitalDetails.setRating("Not Available");
                }

                try {
                    if (jsonObject.getJSONObject("opening_hours").getBoolean("open_now"))  hospitalDetails.setOpeningHours("Opened");
                    else hospitalDetails.setOpeningHours("closed");
                } catch (Exception e) {
                    hospitalDetails.setOpeningHours("Not Available");
                }

                hospitalDetails.setAddress(jsonObject.getString("vicinity"));
                hospitalDetails.setLocationlatlng(new double[]{jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                        jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng")});

                hospdetails.add(hospitalDetails);
            }
        }
        catch(Exception e) {
            Log.d("jsonobject", e.toString());
            hospdetails=null;
        }

    }
}
