package com.example.bookmemoapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenWeatherAPIClient {
    public static String main(Double lat, Double lon) {
        String openWeatherURL = "http://api.openweathermap.org/data/2.5/weather";
        String apiUrl = openWeatherURL + "?lat=" + lat + "&lon=" + lon +"&exclude=current"+"&appid=18f5318dfce0ed53e1654f0a0dd35168";

        // http 연결하고 나머지 하는 부분으로 간다잉
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }

            String readLine;
            StringBuffer response = new StringBuffer();
            while ((readLine = br.readLine()) != null) {
                response.append(readLine);
            }

            br.close();
            System.out.println(response.toString());
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
}
