package com.sanidhyakeluskar.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    final int REQUEST_CODE=123;

    String weather_url = "http://api.openweathermap.org/data/2.5/weather";
    String APP_ID = "20c5cee987794a5dee60b9afc2510284";
    long MIN_TIME = 5000;
    float MIN_DISTANCE = 1000;
    TextView mCity;
    ImageView mWeatherImage;
    TextView mTemperature;

    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCity = (TextView) findViewById(R.id.textView);
        mWeatherImage = (ImageView) findViewById(R.id.imageView3);
        mTemperature = (TextView) findViewById(R.id.textView2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("weatherapp", "onResume() called");
        Log.d("weatherapp", "Getting weather for current location");
        getWeatherForCurrentLocation();
    }

    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("weatherapp", "onLocationChanged() callbcak received");
                String longitude=String.valueOf(location.getLongitude());
                String latitude=String.valueOf(location.getLatitude());
                Log.d("weatherapp","longi is "+longitude);
                Log.d("weatherapp","lati is "+latitude);
                RequestParams params= new RequestParams();
                params.put("lat",latitude);
                params.put("lon",longitude);
                params.put("appid",APP_ID);
                letDoSomeNetworking(params);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("weatherapp", "onProviderDisabled() callback received");

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Log.d("weatherapp","Permission granted");
                getWeatherForCurrentLocation();
            }
            else{
                Log.d("weatherapp","Permission denied");
            }
        }

    }
    private void letDoSomeNetworking(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(weather_url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.d("weatherapp","success "+response.toString());

                WeatherDataModel weatherdata= WeatherDataModel.fromJson(response);
                updateUI(weatherdata);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e,JSONObject response){
                Log.e("weatherapp","Fail "+e.toString());
                Log.d("weatherapp","Status code "+statusCode);
                //Toast.makeText(MainActivity,"Request Failed",Toast.LENGTH_SHORT).show();


            }

        });
    }
    private void updateUI(WeatherDataModel weather){
        mTemperature.setText(weather.getTemperature());
        mCity.setText(weather.getCity());

        int resourceID = getResources().getIdentifier(weather.getIconName(),"drawable",getPackageName());
        mWeatherImage.setImageResource(resourceID);




    }
}
