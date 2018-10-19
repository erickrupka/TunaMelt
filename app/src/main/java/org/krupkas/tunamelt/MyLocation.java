package org.krupkas.tunamelt;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;


public class MyLocation {
    
    Location location;
    
    public MyLocation(Context context){
        // make sure we're allowed to access the location service
        if (!Utils.allowedToUseLocationService(context)) {
            location.reset();
        }

        LocationManager locationManager;
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);

        try {
            location = locationManager.getLastKnownLocation(provider);
            if ((location == null) && (provider.equals(LocationManager.GPS_PROVIDER))) {
                // try again with network
                provider = LocationManager.NETWORK_PROVIDER;
                location = locationManager.getLastKnownLocation(provider);
            }
        } catch (SecurityException e) {
            Log.d("location","cannot get location: " + e.getMessage());
            location.reset();
        }
    }
    
    public Location getLocation(){
        return location;
    }
}
