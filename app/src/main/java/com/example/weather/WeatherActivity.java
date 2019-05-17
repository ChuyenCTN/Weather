package com.example.weather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    private EditText edSearchLocation;
    private ImageView imgMylocation;
    private TextView tvCity;
    private ImageView imgWeather;
    private TextView tvDo;
    private TextView tvDoC;
    private TextView tvDoF;
    private TextView tvStatus;
    private TextView tvDoam;
    private TextView tvGio;
    private TextView tvMay;
    private TextView tvTime;
String doC="";

    private Marker marker;
    protected double longitudeText;
    protected double latitudeText;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        edSearchLocation = (EditText) findViewById(R.id.ed_search_location);
        imgMylocation = (ImageView) findViewById(R.id.img_mylocation);
        tvCity = (TextView) findViewById(R.id.tv_city);
        imgWeather = (ImageView) findViewById(R.id.img_weather);
        tvDo = (TextView) findViewById(R.id.tv_do);
        tvDoC = (TextView) findViewById(R.id.tv_doC);
        tvDoF = (TextView) findViewById(R.id.tv_doF);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvDoam = (TextView) findViewById(R.id.tv_doam);
        tvGio = (TextView) findViewById(R.id.tv_gio);
        tvMay = (TextView) findViewById(R.id.tv_may);
        tvTime = (TextView) findViewById(R.id.tv_time);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setImage(R.drawable.cloud);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location mCurrentLocation = locationResult.getLastLocation();
                LatLng myCoordinates = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                String cityName = getCityName(myCoordinates);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCoordinates, 13.0f));
                if (marker == null) {
                    marker = mMap.addMarker(new MarkerOptions().position(myCoordinates));
                } else
                    marker.setPosition(myCoordinates);
            }
        };
        imgMylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation();
            }
        });
        myLocation();
        tvDoF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WeatherActivity.this, doC, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void searchCity(View view) {
        String locationfind = edSearchLocation.getText().toString();
        List<Address> addressList = null;
        MarkerOptions markerOptions = new MarkerOptions();
        if (locationfind.equalsIgnoreCase("")) {
            Toast.makeText(this, "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
        } else {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(locationfind, 5);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < addressList.size(); i++) {
                Address myAddress = addressList.get(i);
                LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                markerOptions.position(latLng);
                Log.d("lat", latLng.toString());
                markerOptions.title(getCityName(latLng));
                mMap.addMarker(markerOptions);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f));
            }

        }
    }

    private String getCityName(LatLng myCoordinates) {
        String myCity = "";
        String myCity1 = "";
        String myCity2 = "";
        double lat = 0;
        double lon = 0;
        Geocoder geocoder = new Geocoder(WeatherActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(myCoordinates.latitude, myCoordinates.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            myCity = addresses.get(0).getLocality();
            myCity1 = addresses.get(0).getAdminArea();
            myCity2 = addresses.get(0).getFeatureName();
            lat = addresses.get(0).getLatitude();
            lon = addresses.get(0).getLongitude();
            Log.d("mylog", "Complete Address: " + addresses.toString());
            Log.d("mylog", "Address: " + address);
            if (myCity != null) {
                tvCity.setText(myCity);
            } else if (myCity1 != null) {
                tvCity.setText(myCity1);
            } else {
                tvCity.setText(myCity2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        getCurrentWeatherdata(lat, lon);

        return myCity;
    }

    public void myLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("mylog", "Not granted");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else
                requestLocation();
        } else
            requestLocation();

    }

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        Log.d("mylog", "In Requesting Location");
        if (location != null && (System.currentTimeMillis() - location.getTime()) <= 1000 * 2) {
            LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
            String cityName = getCityName(myCoordinates);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setNumUpdates(1);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            Log.d("mylog", "Last location too old getting new location!");
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback, Looper.myLooper());
        }

    }

    public void getCurrentWeatherdata(double lat, double lon) {
        RequestQueue requestQueue = Volley.newRequestQueue(WeatherActivity.this);
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&units=metric&appid=e5033fe5cfe3679717843db42cf9c766";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String day = jsonObject.getString("dt");
                            String name = jsonObject.getString("name");
                            long lday = Long.valueOf(day);
                            Date date = new Date(lday * 1000);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd HH:mm a");
                            SimpleDateFormat simpleDateFormathours = new SimpleDateFormat("HH");
                            SimpleDateFormat simpleDateFormatam = new SimpleDateFormat("a");
                            String Day = simpleDateFormat.format(date);
                            String hours = simpleDateFormathours.format(date);
                            String am = simpleDateFormatam.format(date);
                            int Hours = Integer.parseInt(hours);


                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            String status = jsonObjectWeather.getString("main");
                            String icon = jsonObjectWeather.getString("icon");
                            String imageurl = "http://openweathermap.org/img/w/" + icon + ".png";
                            Picasso.with(WeatherActivity.this).load(imageurl).into(imgWeather);
                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                            String nhietdod = jsonObjectMain.getString("temp");
                            String doam = jsonObjectMain.getString("humidity");
                            Double d = Double.valueOf(nhietdod);
                            String nhietdo = String.valueOf(d.intValue());
                            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                            String gio = jsonObjectWind.getString("speed");
                            JSONObject jsonObjectCloud = jsonObject.getJSONObject("clouds");
                            String may = jsonObjectCloud.getString("all");
                            JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
                            String country = jsonObjectSys.getString("country");
                            tvDo.setText(nhietdo + "°C");
                            nhietdo=doC;
                            tvDoam.setText("Độ ẩm: " + doam + "%");
                            tvStatus.setText(status);
//                            if (status.equalsIgnoreCase("Rain")) {
//                                setImage(R.drawable.rains);
//                            } else if (status.equalsIgnoreCase("clouds")) {
//                                setImage(R.drawable.mostlysunny);
//                            } else if (status.equalsIgnoreCase("clear")) {
//                                setImage(R.drawable.sun);
//                            } else if (status.equalsIgnoreCase("drizzle")) {
//                                setImage(R.drawable.rain);
//                            }


//                            if (Hours<=6){
//                                if (status.equalsIgnoreCase("clear")) {
//                                    setImage(R.drawable.clearnight);
//                                }
//                                if (status.equalsIgnoreCase("cloud")) {
//                                    setImage(R.drawable.cloudnight);
//                                }
//                                if (status.equalsIgnoreCase("clouds")) {
//                                    setImage(R.drawable.mostlycloudynight);
//                                }
//                                if (status.equalsIgnoreCase("Rain")) {
//                                    setImage(R.drawable.heavyrainnight);
//                                }
//                                if (status.equalsIgnoreCase("haze")) {
//                                    setImage(R.drawable.hazenight);
//                                }
//                                if (status.equalsIgnoreCase("drizzle")){
//                                    setImage(R.drawable.drizzlenight);
//                                }
//                            }
//                            if (Hours < 9) {
//                                if (status.equalsIgnoreCase("clear")) {
//                                    setImage(R.drawable.beforeglowclear);
//                                }
//                                if (status.equalsIgnoreCase("cloud")) {
//                                    setImage(R.drawable.cloud);
//                                }
//                                if (status.equalsIgnoreCase("clouds")) {
//                                    setImage(R.drawable.mostlycloudy);
//                                }
//                                if (status.equalsIgnoreCase("Rain")) {
//                                    setImage(R.drawable.heavyrain);
//                                }
//                                if (status.equalsIgnoreCase("haze")) {
//                                    setImage(R.drawable.hazecloudy);
//                                }
//                                if (status.equalsIgnoreCase("drizzle")){
//                                    setImage(R.drawable.drizzle);
//                                }
//                            } else if (Hours >= 9 && Hours <= 17) {
//                                if (status.equalsIgnoreCase("clear")) {
//                                    setImage(R.drawable.mostlysunny);
//                                }
//                                if (status.equalsIgnoreCase("cloud")) {
//                                    setImage(R.drawable.cloud);
//                                }
//                                if (status.equalsIgnoreCase("clouds")) {
//                                    setImage(R.drawable.mostlycloudy);
//                                }
//                                if (status.equalsIgnoreCase("Rain")) {
//                                    setImage(R.drawable.heavyrain);
//                                }
//                                if (status.equalsIgnoreCase("haze")) {
//                                    setImage(R.drawable.haze);
//                                }
//                                if (status.equalsIgnoreCase("drizzle")){
//                                    setImage(R.drawable.drizzle);
//                                }
//                            } else if (Hours > 17 && Hours < 18) {
//                                if (status.equalsIgnoreCase("clear")) {
//                                    setImage(R.drawable.afterglowclear);
//                                }
//                                if (status.equalsIgnoreCase("cloud")) {
//                                    setImage(R.drawable.cloud);
//                                }
//                                if (status.equalsIgnoreCase("clouds")) {
//                                    setImage(R.drawable.mostlycloudy);
//                                }
//                                if (status.equalsIgnoreCase("Rain")) {
//                                    setImage(R.drawable.heavyrain);
//                                }
//                                if (status.equalsIgnoreCase("haze")) {
//                                    setImage(R.drawable.hazecloudy);
//                                }
//                                if (status.equalsIgnoreCase("drizzle")){
//                                    setImage(R.drawable.drizzle);
//                                }
//                            } else if (Hours > 18) {
//                                if (status.equalsIgnoreCase("clear")) {
//                                    setImage(R.drawable.clearnight);
//                                }
//                                if (status.equalsIgnoreCase("cloud")) {
//                                    setImage(R.drawable.cloudnight);
//                                }
//                                if (status.equalsIgnoreCase("clouds")) {
//                                    setImage(R.drawable.mostlycloudynight);
//                                }
//                                if (status.equalsIgnoreCase("Rain")) {
//                                    setImage(R.drawable.rainnight);
//                                }
//                                if (status.equalsIgnoreCase("haze")) {
//                                    setImage(R.drawable.hazenight);
//                                }
//                                if (status.equalsIgnoreCase("drizzle")){
//                                    setImage(R.drawable.drizzlenight);
//                                }
//                            }

                            tvTime.setText(hours + am);
                            tvGio.setText("Gió: " + gio + " m/s");
                            tvMay.setText("Mây: " + may + "%");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    void setImage(int src) {
        Picasso.with(WeatherActivity.this).load(src).into(imgWeather);
    }
}
