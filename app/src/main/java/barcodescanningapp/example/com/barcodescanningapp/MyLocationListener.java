package barcodescanningapp.example.com.barcodescanningapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

final class MyLocationListener implements LocationListener {

    private GoogleMap googleMap;
    private Marker curMarker;

    public MyLocationListener(GoogleMap googleMap, Marker marker){
        this.googleMap = googleMap;
        this.curMarker = marker;
    }
    @Override
    public void onLocationChanged(Location locFromGps) {

        LatLng loc = new LatLng(locFromGps.getLatitude(),locFromGps.getLongitude());
        this.curMarker.remove();
        this.curMarker = googleMap.addMarker(new MarkerOptions()
                .position(loc)
                .draggable(true));
    }

    @Override
    public void onProviderDisabled(String provider) {
        // called when the GPS provider is turned off (user turning off the GPS on the phone)
    }

    @Override
    public void onProviderEnabled(String provider) {
        // called when the GPS provider is turned on (user turning on the GPS on the phone)
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // called when the status of the GPS provider changes
    }
}
