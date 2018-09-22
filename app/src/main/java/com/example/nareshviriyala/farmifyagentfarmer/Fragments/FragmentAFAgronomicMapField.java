package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
;import java.util.Timer;
import java.util.TimerTask;

public class FragmentAFAgronomicMapField extends Fragment implements OnMapReadyCallback{

    private View rootView;
    private LogErrors logErrors;
    private String className;
    private DatabaseHelper dbHelper;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProvideClient;
    private static final float DEFAULT_ZOOM = 19f;
    private Location previousLocation = new Location("");
    private CurrentLocationTimertask currentLocationTimertask;
    public Timer timer;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    private static final float MIN_DISTANCE = 0f;
    private static final int MIN_TIME = 0;

    public FragmentAFAgronomicMapField(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_af_agronomic_fieldmap, container, false);
        try{
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object(){}.getClass().getEnclosingClass().getName();
            ((HomeActivity) getActivity()).setActionBarTitle("Map farm");
            dbHelper = new DatabaseHelper(getActivity());
            previousLocation.setLatitude(0.0d);
            previousLocation.setLongitude(0.0d);
            currentLocationTimertask = new CurrentLocationTimertask();
            timer = new Timer();
            getLocationPermission();
            initMap();

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return rootView;
    }

    private void initMap(){
        try{
            SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    private void getDeviceLocation(){
        try{
            mFusedLocationProvideClient = LocationServices.getFusedLocationProviderClient(getActivity());
            if(mLocationPermissionGranted){
                Task location = mFusedLocationProvideClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location)task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                        }else{
                            Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }catch (SecurityException secEx){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), secEx.getMessage().toString());
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        try{
            Location currentLocation = new Location("");
            currentLocation.setLongitude(latLng.longitude);
            currentLocation.setLatitude(latLng.latitude);
            //float distance = currentLocation.distanceTo(previousLocation);
            if(currentLocation.distanceTo(previousLocation) > 0.5f) {
                previousLocation = currentLocation;
                mMap.addMarker(new MarkerOptions().position(latLng));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
            /*if(mLocationPermissionGranted){
                getDeviceLocation();
                mMap.setMyLocationEnabled(true);
            }*/
            /*locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //if(previousLocation.distanceTo(location) > 0.5) {
                        previousLocation = location;
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(currentLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 19f));
                    //}
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
            getLocationPermission();*/
            Toast.makeText(getActivity(), "Maps ready, Start walking", Toast.LENGTH_SHORT).show();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    private class CurrentLocationTimertask extends TimerTask{

        @Override
        public void run() {
            getDeviceLocation();
        }
    }

    private void getLocationPermission(){
        try{
            if(Build.VERSION.SDK_INT < 23){
                timer.schedule(currentLocationTimertask, 100, 2000);
            }else {
                String[] permissions = {FINE_LOCATION, COURSE_LOCATION};

                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = true;
                        //locationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
                        //initMap();
                        timer.schedule(currentLocationTimertask, 100, 2000);
                    } else {
                        requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
                    }
                } else {
                    requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
                }
            }
        }catch (SecurityException secEx){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), secEx.getMessage().toString());
        }
        catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        try {
            switch (requestCode) {
                case LOCATION_PERMISSION_REQUEST_CODE: {
                    if (grantResults.length > 0) {
                        for (int i = 0; i < grantResults.length; i++) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                mLocationPermissionGranted = false;
                                return;
                            }
                        }
                        mLocationPermissionGranted = true;
                        //initMap();
                        //locationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
                        timer.schedule(currentLocationTimertask, 100, 2000);
                    }
                }
            }
        }catch (SecurityException secEx){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), secEx.getMessage().toString());
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void goBack(){
        try{
            if ( getFragmentManager().getBackStackEntryCount() > 0)
            {
                getFragmentManager().popBackStack();
                return;
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }


}