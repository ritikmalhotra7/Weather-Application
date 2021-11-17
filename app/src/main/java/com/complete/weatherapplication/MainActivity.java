package com.complete.weatherapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.complete.weatherapplication.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;
    public WeatherAdapter adapter;
    public ArrayList<WeatherRVModel> arrayList;
    private LocationManager locationManager;
    private int PERMISSION_CODE= 1;
    private String cityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude(),location.getLatitude());
        getWeatherInfo(cityName);

        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = binding.cityname.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please Enter City Name",Toast.LENGTH_SHORT).show();
                }else{
                    binding.cityname.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Permision Granted!",Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this,"Please Provide the Permissions",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String getCityName (double longitude, double latitude) {
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);
            for (Address adr : addresses) {
                if (adr != null) {
                    String city = adr.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                    } else
                        Log.d("TAG", "CITY NOT FOUND");
                    Toast.makeText(this, "User City Not Found..", Toast.LENGTH_SHORT).show();

                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName){
        String url = "http://api.weatherapi.com/v1/forecast.json?key=58d5be7c691d4b7792f124313211511&q=" + cityName + "&days=1&aqi=no&alerts=no";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                binding.loading.setVisibility(View.GONE);
                binding.relativelayoutid.setVisibility(View.VISIBLE);
                arrayList.clear();
                try{
                    String temp = response.getJSONObject("current").getString("temp_c");
                    binding.temperature.setText("temperature" + "°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("temp_c");
                    Picasso.get().load("http:".concat(conditionIcon)).into(binding.ivicon);
                    binding.condition.setText(condition);

                    if(isDay == 1){
                        Picasso.get().load("https://www.pexels.com/photo/scenic-photo-of-lake-surrounded-by-trees-1903702/").into(binding.imageview);
                    }else{
                        Picasso.get().load("https://www.pexels.com/photo/scenic-view-of-rocky-mountain-during-evening-1624438/").into(binding.imageview);
                    }

                    JSONObject forcastObj = response.getJSONObject("forecast");
                    JSONObject forcastO = forcastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forcastO.getJSONArray("hour");

                    for(int i = 0; i< hourArray.length(); i++){
                        JSONObject hourobj = hourArray.getJSONObject(i);
                        String time = hourobj.getString("time");
                        String temper = hourobj.getString("");
                        String img = hourobj.getJSONObject("condition").getString("icon");
                        String wind = hourobj.getString("wind_kph");

                    }

                    adapter.notifyDataSetChanged();




                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please Enter valid City Name", Toast.LENGTH_SHORT).show();
                Log.d("taget",error.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);



    }
}