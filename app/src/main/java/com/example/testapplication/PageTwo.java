package com.example.testapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class PageTwo extends AppCompatActivity {

    private boolean diagnostic;
    private Localizator localizator;
    private MenuItem switch_lbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diagnostic = false;
        setContentView(R.layout.activity_page_two);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //SENSOR MANAGER
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (barometer != null) {
            sensorManager.registerListener(barometerSensorListener, barometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            openDialog("Couldn't find barometer");
        }
        localizator = new Localizator();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        switch_lbl = menu.findItem(R.id.diagnostic);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.diagnostic) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private SensorEventListener barometerSensorListener = new SensorEventListener() {
        @SuppressLint("DefaultLocale")
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
                TextView pressureValueLabel = findViewById(R.id.pressure_val);
                float pressureValue = event.values[0];
                pressureValueLabel.setText(String.format("%.2f mbar", pressureValue));
                View root = pressureValueLabel.getRootView();

                int colorValue = (Math.round(pressureValue) * 10) % 255;
                int inverse_colorValue = Math.abs(colorValue - 128);

                TextView pressureLabel = findViewById(R.id.pressure_lbl);
                TextView longitude_val = findViewById(R.id.longitude_val);
                TextView latitude_val = findViewById(R.id.latitude_val);
                TextView longitude_lbl = findViewById(R.id.longitude_lbl);
                TextView latitude_lbl = findViewById(R.id.latitude_lbl);
                @SuppressLint("CutPasteId") TextView pressure_lbl = findViewById(R.id.pressure_lbl);
                @SuppressLint("CutPasteId") TextView pressure_val = findViewById(R.id.pressure_val);

                pressureLabel.setTextColor(Color.rgb(inverse_colorValue, inverse_colorValue, inverse_colorValue));
                pressureValueLabel.setTextColor(Color.rgb(inverse_colorValue, inverse_colorValue, inverse_colorValue));
                longitude_val.setTextColor(Color.rgb(inverse_colorValue, inverse_colorValue, inverse_colorValue));
                latitude_val.setTextColor(Color.rgb(inverse_colorValue, inverse_colorValue, inverse_colorValue));
                latitude_lbl.setTextColor(Color.rgb(inverse_colorValue, inverse_colorValue, inverse_colorValue));
                longitude_lbl.setTextColor(Color.rgb(inverse_colorValue, inverse_colorValue, inverse_colorValue));
                pressure_lbl.setTextColor(Color.rgb(inverse_colorValue, inverse_colorValue, inverse_colorValue));
                pressure_val.setTextColor(Color.rgb(inverse_colorValue, inverse_colorValue, inverse_colorValue));

                root.setBackgroundColor(Color.rgb(colorValue, colorValue, colorValue));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    public void openDialog(String message) {
        AlertionDialog alertionDialog = new AlertionDialog(message);
        alertionDialog.show(getSupportFragmentManager(), "Alert");
    }

    private class Localizator {
        double longitude = 0.0, latitude = 0.0;
        boolean enabled;
        LocationManager locationManager;

        Localizator() {
            enabled = false;
        }

        public void enable() {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!isGpsEnabled()) {
                openDialog("GPS is not enabled");
                return;
            }
            if ((ContextCompat.checkSelfPermission(PageTwo.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    &&
                    (ContextCompat.checkSelfPermission(PageTwo.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(PageTwo.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000,
                    1,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            TextView longitude_lbl = findViewById(R.id.longitude_val);
                            longitude_lbl.setText(localizator.getLongitude());
                            TextView latitude_lbl = findViewById(R.id.latitude_val);
                            latitude_lbl.setText(localizator.getLatitude());
                        }
                    });
            enabled = true;
        }

        public boolean isGpsEnabled() {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean gps_enabled;
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            return gps_enabled;
        }

        public String getLongitude() {
            @SuppressLint("DefaultLocale")
            String output = String.format("%.2f", Math.abs(longitude));
            if (longitude >= 0) {
                output += " E";
            } else {
                output += " W";
            }
            return output;
        }

        public String getLatitude() {
            @SuppressLint("DefaultLocale")
            String output = String.format("%.2f", Math.abs(latitude));
            if (longitude >= 0) {
                output += " N";
            } else {
                output += " S";
            }
            return output;
        }
    }

    public void click(View view) {
        int src_id = view.getId();
        if (src_id == R.id.localisation_btn) {
            if (!localizator.enabled) {
                localizator.enable();
            }
        }
        if (src_id == R.id.imageViewBackFromBarometer) {
            Intent intent;
            intent = new Intent(PageTwo.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void switchDiagnostic() {
        diagnostic = !diagnostic;
    }

    public void refresh() {
        if (diagnostic) {
            TextView tmp;
            TextView diag_lbl = findViewById(R.id.diagnostic_lbl2);
            diag_lbl.setVisibility(View.VISIBLE);
            tmp = findViewById(R.id.pressure_lbl);
            tmp.setVisibility(View.VISIBLE);
            tmp = findViewById(R.id.pressure_val);
            tmp.setVisibility(View.VISIBLE);
            switch_lbl.setTitle(R.string.standard);
        } else {
            TextView diag_lbl = findViewById(R.id.diagnostic_lbl2);
            diag_lbl.setVisibility(View.INVISIBLE);
            TextView tmp;
            tmp = findViewById(R.id.pressure_lbl);
            tmp.setVisibility(View.INVISIBLE);
            tmp = findViewById(R.id.pressure_val);
            tmp.setVisibility(View.INVISIBLE);
            switch_lbl.setTitle(R.string.diagnostic);
        }
    }

    public void click(MenuItem item) {
        int src_id = item.getItemId();
        if (src_id == R.id.diagnostic) {
            switchDiagnostic();
            refresh();
        }
    }
}