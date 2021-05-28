package com.barber.app.utils;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import com.barber.app.constants.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class LocationManager extends Activity {


    private final Activity context;
    private final boolean checkPermissions;
    private final boolean openLocationSettings;
    private int PERMISSION_ID = 44;
    private FusedLocationProviderClient mFusedLocationClient;

    public LocationManager(Activity context, boolean checkPermissions, boolean openLocationSettings) {
        this.context = context;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.checkPermissions = checkPermissions;
        this.openLocationSettings = openLocationSettings;
        getLastLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        This method is called when a user Allow or Deny our
//        requested permissions. So it will help us to move forward
//        if the permissions are granted.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        if (GPSUtils.checkPermissions(context)) {
            if (GPSUtils.isLocationEnabled(context)) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    saveLastKnownLoc(location);
                                    requestNewLocationData();
                                }
                            }
                        }
                );
            } else {
                if (openLocationSettings) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            }
        } else {
            if (checkPermissions) {
                requestPermissions();
            }
        }
    }

    private void saveLastKnownLoc(Location location) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHOP_PACK, MODE_PRIVATE).edit();
        editor.putString(Constants.GPS_LAT, String.valueOf(location.getLatitude()));
        editor.putString(Constants.GPS_LON, String.valueOf(location.getLongitude()));
        editor.apply();
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(5000);
//        mLocationRequest.setNumUpdates(100);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            saveLastKnownLoc(mLastLocation);
        }
    };

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                context,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_ID
        );
    }

}
