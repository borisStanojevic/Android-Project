package com.example.student.myproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final String locationProvider = LocationManager.NETWORK_PROVIDER;
    private static String lastKnownLocation = "";
    private Geocoder geocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String[] lastKnownTokens = lastKnownLocation.split("-");
                    if (!lastKnownTokens[0].equals(addresses.get(0).getLocality()) && !lastKnownTokens[1].equals(addresses.get(1).getCountryName()))
                        ((TextView) findViewById(R.id.tv_test)).setText(addresses.get(0).getLocality() + "-" + addresses.get(0).getCountryName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        geocoder = new Geocoder(this);

    }

    public void getLocation(View view) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 99);
        } else {
//            locationManager.requestLocationUpdates(locationProvider, 0,0,locationListener);
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            try {
                List<Address> addresses = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
                TestActivity.lastKnownLocation = addresses.get(0).getLocality() + "-" + addresses.get(0).getCountryName();
                ((TextView) findViewById(R.id.tv_test)).setText(addresses.get(0).getLocality() + "\t" + addresses.get(0).getCountryName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 99: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                        ((TextView) findViewById(R.id.tv_test)).setText(lastKnownLocation.toString());
                    } catch (SecurityException exc) {

                    }
                } else {

                }
                return;
            }

        }
    }
}
