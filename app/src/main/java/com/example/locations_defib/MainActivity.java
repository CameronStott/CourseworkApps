package com.example.locations_defib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.locations_defib.directionhelpers.FetchURL;
import com.example.locations_defib.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.protobuf.DescriptorProtos;
import com.karumi.dexter.Dexter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    GoogleMap map , mapDrive, mapWalk;
    Button btnDriving;
    Button btnWalking;

    MarkerOptions place1;
    MarkerOptions place2;

    public List<LatLng> markList = new ArrayList<LatLng>();

    // Create a global variable for the system Location Manager and Listener
    LocationManager locationManager;
    LocationListener locationListener;
    // The identifier for which permission request has been answered.
    private static final int ACCESS_REQUEST_LOCATION = 0;

    private static final String TAG = "MyActivity";
    TextView textView;
    public List<DefibLocation> locationList = new ArrayList<DefibLocation>();
    //static double[] dist = new double[2];

    Polyline currentPolyLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);

        readLocationData();
        textView = findViewById(R.id.location_info);


    }



    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;


        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                if (location != null) {
                    String locationString =
                            "Location changed: Lat: " + location.getLatitude() +
                                    " Lng: " + location.getLongitude();

                    Toast.makeText(getBaseContext(), locationString, Toast.LENGTH_LONG).show();

                    DefibLocation dist = findClosestLatLon(location.getLatitude(),location.getLongitude());
                    //Log.d(TAG, "inner" + points[0] + points[1]);
                    textView.setText(dist.getLocation());


                    place1 = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Location 1");
                    place2 = new MarkerOptions().position(new LatLng(dist.getLatitude(), dist.getLongitude())).title("Position 2");

                    map.addMarker(place1);
                    map.addMarker(place2);

                    Log.d(TAG ,"loc" + place2);
                    //map.clear();
                    //taken from https://stackoverflow.com/questions/16458900/google-maps-api-v2-zooming-near-the-marker
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),15));
                    // Zoom in, animating the camera.
                    map.animateCamera(CameraUpdateFactory.zoomIn());
                    // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                    map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

                    btnDriving = findViewById(R.id.btnDriving);
                    btnDriving.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new FetchURL(MainActivity.this, "driving")
                                    .execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");

                        }
                    });

                    btnWalking = findViewById(R.id.btnWalking);
                    btnWalking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new FetchURL(MainActivity.this,"walking")
                                    .execute(getUrl(place1.getPosition(),place2.getPosition(), "walking"),"walking");
                        }
                    });
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) { }
            public void onProviderEnabled(String provider) {
            }
            public void onProviderDisabled(String provider) { }


        };

        // Handle the case we don't have the necessary permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(getResources().getString(R.string.app_name),
                    "No Location Permission. Asking for Permission");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_REQUEST_LOCATION);
        } else {
            Log.i(getResources().getString(R.string.app_name),
                    "Location is allowed.");
            setLocationUpdateFunction();
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCESS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted.
                    Log.i(getResources().getString(R.string.app_name),
                            "Location Permission granted by user.");
                    setLocationUpdateFunction();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.e(getResources().getString(R.string.app_name),
                            "No Location Permission granted by user.");
                }
                return;
            }

        }
    }

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 second
    //    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    @SuppressLint("MissingPermission")
    private void setLocationUpdateFunction() {
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
    }






    public DefibLocation findClosestLatLon(double latitude, double longitude){
        double lat2;
        double lon2;

        double distance = 100000;
        int counter = -1;

       for (int i = 0; i < locationList.size(); i++) {
            lat2 = locationList.get(i).getLatitude();
            lon2 = locationList.get(i).getLongitude();

            if (distance > findDistance(latitude,longitude,lat2,lon2)){
                distance = findDistance(latitude,longitude,lat2,lon2);
                counter = i;

            }
        }
        DefibLocation location = locationList.get(counter);

        return location;
    }


    //code used from https://gist.github.com/vananth22/888ed9a22105670e7a4092bdcf0d72e4
    public double findDistance(Double lat1,Double lon1, Double lat2,Double lon2){
        final int R = 6371; // Radious of the earth
        Double latDistance = toRad(lat2-lat1);
        Double lonDistance = toRad(lon2-lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distance = R * c;
        return distance;
    }

    //code used from https://gist.github.com/vananth22/888ed9a22105670e7a4092bdcf0d72e4
    private static Double toRad(Double value) {


        return value * Math.PI / 180;
    }

    private void readLocationData() {
        InputStream inputStream = getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

        String line = "";
        try{
            while((line = reader.readLine()) != null) {
                //split by ','
                String[] tokens = line.split(",");

                //read data
                DefibLocation location = new DefibLocation();
                location.setLocation(tokens[0]);
                location.setAddress(tokens[1]);
                location.setCityTown(tokens[2]);
                location.setCounty(tokens[3]);
                location.setPostcode(tokens[4]);
                if (tokens[5].length() > 0){
                    location.setLatitude(Double.parseDouble(tokens[5]));
                }else{
                    location.setLatitude(0.0);
                }
                if (tokens[6].length() > 0){
                    location.setLongitude(Double.parseDouble(tokens[6]));
                }else{
                    location.setLongitude(0.0);
                }
                locationList.add(location);

               // Log.d("MyActivity", "Just inputted" + location);

            }
            }catch (IOException e) {
                Log.wtf("MyActivity", "Error reading data file" + line, e);
                e.printStackTrace();
            }
        }

    private String getUrl (LatLng origin, LatLng dest, String directionMode){
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String mode = "mode=" + directionMode;

        String parameters = str_origin + "&" + str_dest + "&" + mode;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters +"&key=" + getString(R.string.google_maps_key);

        //String url = "https://www.google.com/maps/dir/?api=1&"+str_origin+"&"+str_dest+"&"+mode;
//        Log.d(TAG,"getUrl" + markList.get(1));
        return url;



    }


    @Override
    public void onTaskDone(Object... values) {
        if(currentPolyLine!= null){
            currentPolyLine.remove();
            currentPolyLine = map.addPolyline((PolylineOptions) values[0]);
        }else {
            currentPolyLine = map.addPolyline((PolylineOptions) values[0]);
        }
    }
}
