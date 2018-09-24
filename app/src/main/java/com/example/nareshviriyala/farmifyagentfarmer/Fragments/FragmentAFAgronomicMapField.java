package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentAFAgronomicMapField extends Fragment implements OnMapReadyCallback, View.OnClickListener, Switch.OnCheckedChangeListener{

    private View rootView;
    private LogErrors logErrors;
    private String className;
    private DatabaseHelper dbHelper;
    private TextView tv_temp;
    private LocationCallback mLocationCallBack;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location previousLocation = new Location("");
    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 19f;
    private Bitmap bmDot;
    private Switch sw_mapfarm;
    private ArrayList<LatLng> polyLine = new ArrayList<LatLng>();
    private ArrayList<LatLng> polyLine_backup = new ArrayList<LatLng>();
    public JSONObject farmeragronomicDataItem;
    private JSONArray farmeragronomicData;
    private int currentItemId = 0;
    private JSONArray coordinates = new JSONArray();
    private ImageView img_savemap, img_clearmap, img_currentloaction;

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
            tv_temp = rootView.findViewById(R.id.tv_temp);
            sw_mapfarm = rootView.findViewById(R.id.sw_mapfarm);
            sw_mapfarm.setOnCheckedChangeListener(this);
            previousLocation.setLatitude(0.0d);
            previousLocation.setLongitude(0.0d);
            Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blackdot);
            bmDot = Bitmap.createScaledBitmap(imageBitmap, 50, 50, false);
            img_savemap = rootView.findViewById(R.id.img_savemap);
            img_clearmap = rootView.findViewById(R.id.img_clearmap);
            img_currentloaction = rootView.findViewById(R.id.img_currentloaction);

            img_savemap.setOnClickListener(this);
            img_clearmap.setOnClickListener(this);
            img_currentloaction.setOnClickListener(this);

            String data = dbHelper.getParameter(getString(R.string.Agronomic));
            if(data.isEmpty() || data == null || data.equalsIgnoreCase("[]"))
                farmeragronomicData = new JSONArray();
            else
                farmeragronomicData = new JSONArray(data);


            currentItemId = getArguments().getInt("ItemId");
            farmeragronomicDataItem = farmeragronomicData.getJSONObject(currentItemId);

            if(farmeragronomicDataItem.has("FarmMap"))
                coordinates = farmeragronomicDataItem.getJSONArray("FarmMap");

            initMap();
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

            // Create the LocationRequest object
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(1000)        // 2 seconds, in milliseconds
                    .setFastestInterval(1 * 1000); // 1 second, in milliseconds

            mLocationCallBack = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        handleNewLocation(location, 0);
                    }
                };
            };
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return rootView;
    }

    private void getDeviceLocation(){
        try{
            Task location = mFusedLocationClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Location currentLocation = (Location)task.getResult();
                        handleNewLocation(currentLocation, DEFAULT_ZOOM);
                    }else{
                        Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (SecurityException secEx){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), secEx.getMessage().toString());
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    private void initMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        try {
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
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
            //mMap.setMyLocationEnabled(true);
            //mMap.getUiSettings().setMyLocationButtonEnabled(true);
            if(coordinates.length()>0)
                plotFarmFromCoordinates();
            else
                getDeviceLocation();
        }catch (SecurityException secEx){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), secEx.getMessage().toString());
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    private void plotFarmFromCoordinates(){
        try{
            for(int i = 0; i< coordinates.length(); i++){
                JSONArray coordinate = coordinates.getJSONArray(i);
                LatLng cord = new LatLng(coordinate.getDouble(0), coordinate.getDouble(1));
                polyLine.add(cord);
                mMap.clear();
                mMap.addPolyline(new PolylineOptions().addAll(polyLine).width(15f).color(Color.WHITE));
                //mMap.addPolygon(new PolygonOptions().addAll(polyLine).fillColor(Color.WHITE).strokeColor(Color.WHITE));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polyLine.get(0), DEFAULT_ZOOM));
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    private void handleNewLocation(Location currentLocation, float zoom) {
        try {
            double currentLatitude = currentLocation.getLatitude();
            double currentLongitude = currentLocation.getLongitude();
            LatLng latLng = new LatLng(currentLatitude, currentLongitude);
            tv_temp.setText("lat: "+String.valueOf(currentLocation.getLatitude())+"\nlong: "+String.valueOf(currentLocation.getLongitude()));
            if(zoom > 0) {
                //mMap.clear() line should below and not before if else statement
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bmDot)));
                mMap.addPolyline(new PolylineOptions().addAll(polyLine).width(15f).color(Color.WHITE));
                //mMap.addPolygon(new PolygonOptions().addAll(polyLine).fillColor(Color.WHITE));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
            }else if(currentLocation.distanceTo(previousLocation) > 0.5f) {
                mMap.clear();
                previousLocation = currentLocation;
                polyLine.add(latLng);
                mMap.addPolyline(new PolylineOptions().addAll(polyLine).width(15f).color(Color.WHITE));
                //mMap.addPolygon(new PolygonOptions().addAll(polyLine).fillColor(Color.WHITE));
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void clearMap(final boolean startmappingagain){
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Existing map will be erased.\nAre you sure?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                mMap.clear();
                                if(polyLine.size() > 0)
                                    polyLine_backup = polyLine;
                                polyLine.clear();
                                if(startmappingagain) {
                                    ((TextView) rootView.findViewById(R.id.tv_mapping)).setText("Mapping...");
                                    startMapping();
                                }
                                dialog.cancel();
                            }catch (Exception ex){
                                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
                            }
                        }
                    });

            builder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sw_mapfarm.setChecked(false);
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void startMapping(){
        try{
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallBack,null /* Looper */);
        }catch (SecurityException secEx){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), secEx.getMessage().toString());
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void stopMapping(){
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallBack);

        }catch (SecurityException secEx){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), secEx.getMessage().toString());
        }
        catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

       @Override
    public void onResume() {
        super.onResume();
        if(sw_mapfarm.isChecked())
            startMapping();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            stopMapping();
        }
        catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    @Override
    public void onClick(View v) {
        try{
            switch (v.getId()){
                case R.id.img_savemap:
                    sw_mapfarm.setChecked(false);
                    String data = "";
                    if(polyLine.size() > 0)
                        data =  polyLine.toString();
                    else if(polyLine_backup.size() > 0)
                        data = polyLine_backup.toString();
                    data = data.replace("lat/lng:","").replace("(","[").replace(")","]");
                    JSONArray map = new JSONArray(data);
                    farmeragronomicDataItem.put("FarmMap", map);
                    farmeragronomicData.put(currentItemId, farmeragronomicDataItem);
                    dbHelper.setParameter(getResources().getString(R.string.Agronomic), farmeragronomicData.toString());
                    goBack();
                    break;
                case R.id.img_clearmap:
                    clearMap(false);
                    break;
                case R.id.img_currentloaction:
                    getDeviceLocation();
                    break;
            }
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            if(polyLine.size() > 0)
                clearMap(true);
            else {
                ((TextView) rootView.findViewById(R.id.tv_mapping)).setText("Mapping...");
                startMapping();
            }
        }
        else {
            ((TextView)rootView.findViewById(R.id.tv_mapping)).setText("Start mapping");
            stopMapping();
        }
    }
}