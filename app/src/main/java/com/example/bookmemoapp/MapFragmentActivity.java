package com.example.bookmemoapp;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.ZoomControlView;

import org.json.JSONObject;

public class MapFragmentActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private FusedLocationSource mLocationSource;
    private NaverMap mNaverMap;

    TextView marker1;
    boolean availMarker1 = false;

    TextView weatherMain, weatherTemp, weatherHumidity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_fragment);

        marker1 = findViewById(R.id.marker1);
        marker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 마커 찍을 수 없는 상태
                if (availMarker1) {
                    marker1.setTextColor(Color.parseColor("#35881E"));
                    marker1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    availMarker1 = false;
                }
                // 마커 찍을 수 있는 상태
                else {
                    marker1.setTextColor(Color.parseColor("#FFFFFF"));
                    marker1.setBackgroundColor(Color.parseColor("#35881E"));
                    availMarker1 = true;
                }
            }
        });

        weatherMain = findViewById(R.id.weatherMain);
        weatherTemp = findViewById(R.id.weatherTemp);
        weatherHumidity = findViewById(R.id.weatherHumidity);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    class WeatherThread extends AsyncTask<Double, String, String> {

        @Override
        protected String doInBackground(Double... str) {
            return OpenWeatherAPIClient.main(str[0], str[1]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject weatherJo = jsonObject.getJSONArray("weather").getJSONObject(0);
                String mainWeather = weatherJo.getString("main");
                JSONObject mainJo = jsonObject.getJSONObject("main");
                Double temp = mainJo.getDouble("temp");
                Double celcius = Math.round((temp-273.15)*10)/10.0;
                int humidityInt = mainJo.getInt("humidity");


                weatherMain.setText(mainWeather);
                weatherTemp.setText(String.valueOf(celcius));
                weatherHumidity.setText(String.valueOf(humidityInt));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        UiSettings uiSettings = naverMap.getUiSettings();

        Marker marker = new Marker();
        naverMap.setOnMapClickListener((point, coord) -> {
            if (availMarker1) {
                marker.setPosition(new LatLng(coord.latitude, coord.longitude));
                new WeatherThread().execute(coord.latitude, coord.longitude);
                marker.setMap(naverMap);
            }
        });

        /*
        marker.setOnClickListener(overlay -> {
            Toast.makeText(this, "마커 1 클릭", Toast.LENGTH_SHORT).show();
            // 이벤트 소비, OnMapClick 이벤트는 발생하지 않음
            return true;
        });
         */

        // NaverMap 객체 받아서 NaverMap 객체에 위치 소스 지정
        mNaverMap = naverMap;
        mNaverMap.setLocationSource(mLocationSource);

        // 권한확인. 결과는 onRequestPermissionsResult 콜백 매서드 호출
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        ZoomControlView zoomControlView = findViewById(R.id.zoom);
        zoomControlView.setMap(mNaverMap);
        uiSettings.setLocationButtonEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // request code와 권한획득 여부 확인
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}