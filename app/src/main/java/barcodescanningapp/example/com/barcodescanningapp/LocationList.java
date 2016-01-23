package barcodescanningapp.example.com.barcodescanningapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class LocationList extends Activity {
    protected Context context;
    ListView listView ;
    private Double destLat = 0.0;
    private Double destLng = 0.0;
    private LatLng userLocation;
    Intent intent;
    Timer timer1;
    Timer timer2;
    LocationManager service;
    LocationManager serviceNew;
    Location location;
    Marker curMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        SharedPreferences settingsKey = getSharedPreferences("MyPrefsKeyFile", 0);
        String keys = settingsKey.getString("MyPrefsKey", "");
        String[] strVal = keys.split("%%%");

        listView = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, strVal);

        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);

                SharedPreferences shared = getSharedPreferences("MyPrefsFile", 0);
                String storedData = (shared.getString(itemValue, ""));

                String[] parts = storedData.split("%");
                String strLat = parts[0];
                String strLng = parts[1];
                destLat = Double.parseDouble(strLat);
                destLng = Double.parseDouble(strLng);

//                Intent intent1 = new Intent(LocationList.this, PathGoogleMapActivity.class);
//                LocationListener locationListener = new MyLocationListener(intent1,destLat,destLng,LocationList.this);
//                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locationListener);


                //Intent intent = new Intent(LocationList.this, GetCurrentLocation.class);
                intent = new Intent(LocationList.this, PathGoogleMapActivity.class);

                intent.putExtra("destLat", destLat);
                intent.putExtra("destLng", destLng);

                getCurrentLocationCoordinates();

//                intent.putExtra("curLat", userLoc.latitude);
//                intent.putExtra("curLng", userLoc.longitude);
//
//                intent.putExtra("notDefault", true);
//                LocationList.this.startActivity(intent);

//                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng);
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                LocationList.this.startActivity(intent);

            }

        });
    }

    private void getCurrentLocationCoordinates(){

        service = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if(service.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            service.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
            //location = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            timer1 = new Timer();
            timer1.schedule(new useNetworkProvider(), 10000);
        }

    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            doExtra(location);
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
            doExtra(location);
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
                Log.d("andr", ex.getMessage());
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
            doExtra(location);
        }
    }

    private void doExtra(Location userLoc)
    {
        intent.putExtra("curLat", userLoc.getLatitude());
        intent.putExtra("curLng", userLoc.getLongitude());

        intent.putExtra("notDefault", true);
        LocationList.this.startActivity(intent);
    }

}
