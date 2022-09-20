package com.example.bookmemoapp.bestseller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BestSellerAPI {
    public static String main(String categoryId) {
        String clientKey = "19181A805A5EF44509B94291F12E8A9DD0F049698380CE9FD4781BC3303F42FB";
        try {
            String apiURL = "http://book.interpark.com/api/bestSeller.api?";
            apiURL += "key="+clientKey;
            apiURL += "&categoryId="+categoryId;
            apiURL += "&output=json";

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
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
