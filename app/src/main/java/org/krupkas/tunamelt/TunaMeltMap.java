package org.krupkas.tunamelt;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;


public class TunaMeltMap extends Activity implements OnMapReadyCallback {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.show_tm_map);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        DbAdapter db = new DbAdapter(getApplicationContext());
        db.open();

        ArrayList<TMRecord> tmRecords = db.getAllRecords(DbAdapter.DB_COLUMN_RESTAURANT,true);
        db.close();

        MyLocation myLocation = new MyLocation((Context)this);
        Location location = myLocation.getLocation();

        LatLng loc;

        for (TMRecord tmRec : tmRecords){
            loc =  new LatLng(tmRec.getLatitude(),tmRec.getLongitude());

            googleMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .title(tmRec.getRestaurant())
                    .snippet("Rating: " + tmRec.getRating())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }

        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        float cameraZoom = 12;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (location != null){
            LatLng curLatLng = new LatLng(location.getLatitude(),location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, cameraZoom));
        }
    }


    @Override
     public void onResume(){
         super.onResume();     
     }
         
         
     @Override
     public void onPause(){
         super.onPause();
     }

         
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getActionBar().setDisplayHomeAsUpEnabled(true);
         return true;
     }
         
         
     @Override
     public boolean onOptionsItemSelected(MenuItem item) 
     {
         switch (item.getItemId()) {
         case android.R.id.home: 
             onBackPressed();
             break;

         default:
             return super.onOptionsItemSelected(item);
         }
         return true;
     }
     
}
