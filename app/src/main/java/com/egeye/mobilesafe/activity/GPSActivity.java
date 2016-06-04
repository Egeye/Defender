package com.egeye.mobilesafe.activity;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Bundle;

import com.egeye.mobilesafe.R;

/**
 * Created by Octavio on 2016/2/4.
 */
public class GPSActivity extends Activity {

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gps);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


    }
}
