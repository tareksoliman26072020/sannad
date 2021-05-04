package com.Sannad.SannadApp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.Sannad.SannadApp.Activity.AccountSettingActivity;
import com.Sannad.SannadApp.Activity.MainActivity;

import pub.devrel.easypermissions.EasyPermissions;

/**This class is for the tracking of the location.*/
public class TrackerService extends Service implements LocationListener {

    /**Latitude.*/
    private Double latitude;

    /**Longitude.*/
    private Double longitude;

    /**The context (activity).*/
    private Context context;

    /**The Location.*/
    private Location location;

    /**The Location manager.*/
    private LocationManager locationManager;

    /**Required a empty constructor.*/
    public TrackerService() {}

    public TrackerService(Context context) {
        this.context = context;
        location = locate();
    }

    /**Determine the location.*/
    @SuppressLint("MissingPermission")
    private Location locate() {
        if(EasyPermissions.hasPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,})) {
            Location location = null;
            locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSActive = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetActive = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.d("Reeeeeeee", "" + isGPSActive + " " + isNetActive + " " + locationManager + "111");


            if (isNetActive) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 20, this);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.d("Reeeeeeee", "" + isGPSActive + " " + isNetActive + " " + locationManager + "222");
            } else if (isGPSActive) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 20, this);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Log.d("Reeeeeeee", "" + isGPSActive + " " + isNetActive + " " + locationManager + "333");
            }

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }else{
            Toast.makeText(context, "Your address was not located, you need to allow access location permission.", Toast.LENGTH_LONG).show();

        }
        return location;
    }

    /**Stop updating.*/
    public void stopUpdates(){
        if(locationManager != null)
            locationManager.removeUpdates(this);
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

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
}
