package barcodescanningapp.example.com.barcodescanningapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class BarcodeScanningAppExample extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the main content layout of the Activity
        setContentView(R.layout.activity_main_layout);
    }

    public void launchBarcodeActivity(View v) {
        try {
            //setContentView(R.layout.activity_barcode_reader);
            Intent intent = new Intent(this, BarcodeCapture.class);
            this.startActivity(intent);

        } catch (ActivityNotFoundException anfe) {

        }
    }
    public void launchLocationActivity(View v) {
        try {
            //setContentView(R.layout.activity_barcode_reader);
            Intent intent = new Intent(this, GetCurrentLocation.class);
            this.startActivity(intent);

        } catch (ActivityNotFoundException anfe) {

        }
    }

    public void launchLocationList(View v) {
        try {
            Intent intent = new Intent(this, LocationList.class);
            this.startActivity(intent);

        } catch (ActivityNotFoundException anfe) {

        }
    }

    public void launchDirectionActivity(View v) {
        try {
            Intent intent = new Intent(this, PathGoogleMapActivity.class);
            this.startActivity(intent);

        } catch (ActivityNotFoundException anfe) {

        }
    }


}
