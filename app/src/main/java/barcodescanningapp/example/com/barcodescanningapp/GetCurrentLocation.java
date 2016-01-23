package barcodescanningapp.example.com.barcodescanningapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.preference.*;
import java.io.*;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class GetCurrentLocation extends Activity{

    protected Context context;
    private GoogleMap map;
    private LatLng userLocation;
    Location location;
    Timer timer1;
    Timer timer2;
    LocationManager service;
    LocationManager serviceNew;
    Marker curMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        double lat = 0.0;
        double lng = 0.0;
        boolean notDefault = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_current_location);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        if(getIntent().getExtras() != null) {
            lat = getIntent().getExtras().getDouble("lat");
            lng = getIntent().getExtras().getDouble("lng");
            notDefault = getIntent().getExtras().getBoolean("notDefault");
        }

        if(notDefault)
            this.renderMap(lat,lng);
        else
            this.renderMap();
    }

    private void renderMapNew(double lat,double lng){
        userLocation = new LatLng(lat,lng);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        Marker hamburg = map.addMarker(new MarkerOptions().position(userLocation)
                .title("Hamburg"));
        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    private void renderMap(){

        service = (LocationManager) getSystemService(context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        //String provider = service.getBestProvider(criteria, false);
        if(service.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            service.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
            //location = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            timer1 = new Timer();
            timer1.schedule(new useNetworkProvider(), 10000);
        }
    }

    private void renderMap(double lat, double lng){
        userLocation = new LatLng(lat,lng);
        if(curMarker != null)
            curMarker.remove();

        Marker hamburg = map.addMarker(new MarkerOptions().position(userLocation)
                .title("Hamburg"));
        curMarker = hamburg;
        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    public void captureLocation(View v){

        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
        SharedPreferences.Editor editor = settings.edit();
        this.showInputDialog(editor);
        String dataToStore = Double.toString(this.userLocation.latitude) + "%" + Double.toString(this.userLocation.longitude);
    }
    private String m_Text = "";
    private void showInputDialog(final SharedPreferences.Editor editor){
        final String dataToStore = Double.toString(this.userLocation.latitude) + "%" + Double.toString(this.userLocation.longitude);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                editor.putString(m_Text, dataToStore);
                editor.commit();

                SharedPreferences settingsKey = getSharedPreferences("MyPrefsKeyFile", 0);

                if(settingsKey.getString("MyPrefsKey","").length()  == 0){
                    SharedPreferences.Editor editor1 = settingsKey.edit();
                    editor1.putString("MyPrefsKey", m_Text);
                    editor1.commit();
                }
                else {
                    SharedPreferences.Editor edit = settingsKey.edit();
                    edit.putString("MyPrefsKey", settingsKey.getString("MyPrefsKey","") + "%%%" + m_Text);
                    edit.apply();

                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            renderMap(location.getLatitude(), location.getLongitude());
            service.removeUpdates(this);
            service.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer2.cancel();
            renderMap(location.getLatitude(), location.getLongitude());
            service.removeUpdates(this);
            service.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    class useNetworkProvider extends TimerTask {
        @Override
        public void run() {
            try {
                service.removeUpdates(locationListenerGps);
                serviceNew = (LocationManager) getSystemService(context.LOCATION_SERVICE);
                if (serviceNew.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    serviceNew.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork, Looper.getMainLooper());
                    //location = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    timer2 = new Timer();
                    timer2.schedule(new getLastKnownLocation(), 10000);
                }
            }
            catch (Exception ex)
            {
                Log.d("andr",ex.getMessage());
            }
        }
    }

    class getLastKnownLocation extends TimerTask {
        @Override
        public void run() {
            service.removeUpdates(locationListenerGps);
            service.removeUpdates(locationListenerNetwork);
            if(service.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                location = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            else if(service.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                location = service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            renderMap(location.getLatitude(), location.getLongitude());
        }
    }
}
