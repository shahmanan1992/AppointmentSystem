package com.manan.appointment.User;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Manan on 7/27/2016.
 */
public class GPSTracker extends Service {
    private final Context context;
    static boolean isGPSEnabled = false;
    static boolean isNetworkEnabled = false;
    static boolean canGetLocation = false;
    private static double lat, longi;
    private static final long MIN_DIST_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BTW_UPDATES = 100 * 60 * 1;

    protected LocationManager locationManager;
    private LocationListener locListener = new MyLocationListener();
//    Location location;

    public GPSTracker(Context context) {
        this.context = context;
        getLocation();
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                canGetLocation = false;
            } else {
                canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            0, 0, locListener);
//                    if (locationManager != null) {
//                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        if (location != null) {
//                            lat = location.getLatitude();
//                            longi = location.getLongitude();
//                            Log.i("NetworkEnabled", "inside function");
//                        }
//                        Log.i("NetworkEnabled", lat + "," + longi);
//                    }
                }

                if (isGPSEnabled) {
//                    if(location==null)
//                    {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                            0, locListener);
                }
            }
            //           }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
 //       return location;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    public void stopUsingGPS() {
//        try {
//            if (locationManager != null) {
//                locationManager.removeUpdates(GPSTracker.this);
//            }
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }
//
//    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return longi;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("GPS Settings");
        alertDialog.setMessage("GPS is not enabled. Want to go to settings?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.show();

    }


    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            try {
                if (location != null) {
                    lat = location.getLatitude();
                    longi = location.getLongitude();
                    locationManager.removeUpdates(locListener);
                    Log.i("GPSEnabled", "inside function");
                }
                Log.i("GPSEnabled", lat + "," + longi);
            }
            catch(SecurityException e)
            {
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


    }
}