package com.nickderonde.bottletime;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationListener locationListener;
    LocationManager locationManager;
    public final static String LOG_TAG = "Bottle Maps";


    /**
     * check for permissions onRequestPremissionResult
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //check if permission is granted
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15, 15, locationListener);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {

            /**
             *
             *
             * @param location
             */
            @Override
            public void onLocationChanged(Location location) {
                // Add a marker to gps location and move the camera
                //clear markers on map
                mMap.clear();

                //show gps location
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(myLocation).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 10));

                //add some bottle stores to the map
                LatLng store = new LatLng(43.6453, -79.3946);
                mMap.addMarker(new MarkerOptions().position(store).title("King & Spadina, Toronto"));

                LatLng store1 = new LatLng(43.842, -79.0223);
                mMap.addMarker(new MarkerOptions().position(store1).title("Bayly & Harwood, Ajax"));

                LatLng store2 = new LatLng(48.452, -89.2513);
                mMap.addMarker(new MarkerOptions().position(store2).title("Dawson & Hwy 11 / 17, Thunder Bay"));

                LatLng store3 = new LatLng(43.6454, -79.7552);
                mMap.addMarker(new MarkerOptions().position(store3).title("Mavis & Steeles, Brampton"));

                LatLng store4 = new LatLng(43.6754, -79.5571);
                mMap.addMarker(new MarkerOptions().position(store4).title("Eglinton & Kipling, Etobicoke"));

                LatLng store5 = new LatLng(43.774, -79.2797);
                mMap.addMarker(new MarkerOptions().position(store5).title("Kennedy & 401, Scarborough"));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                permissions();
            }

            @Override
            public void onProviderEnabled(String provider) {
                permissions();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i(LOG_TAG, "onProviderDisabled");
            }
        };
        permissions();
    }


    /**
     * Check for permissions and ask for them when needed
     *
     */
    public void permissions() {
        // Check and ask for the permissions
        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15, 15, locationListener);
        } else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    ContexCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15, 15, locationListener);

                // Give the lastKnow location.
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                LatLng myLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                //clear markers on map
                mMap.clear();

                mMap.addMarker(new MarkerOptions().position(myLocation).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 10));
            }
        }
    }
}
